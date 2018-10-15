package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.CreditEnum;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.constant.LoanFileEnum;
import com.yunche.loan.config.constant.TermFileEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.feign.client.ICBCFeignNormal;
import com.yunche.loan.config.util.DateUtil;
import com.yunche.loan.config.util.FtpUtil;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.ViolationUtil;
import com.yunche.loan.domain.entity.BankCreditInfoDO;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.entity.LoanCreditInfoDO;
import com.yunche.loan.domain.param.ICBCApiCallbackParam;
import com.yunche.loan.domain.vo.UniversalBankInterfaceFileSerialDO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.BankSolutionProcessService;
import com.yunche.loan.service.LoanCreditInfoService;
import com.yunche.loan.service.LoanProcessService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

import static com.yunche.loan.config.constant.LoanCustomerConst.CREDIT_TYPE_BANK;

@Service
@Transactional
public class BankSolutionProcessServiceImpl implements BankSolutionProcessService {
    private static final Logger logger = LoggerFactory.getLogger(BankSolutionProcessServiceImpl.class);
    @Resource
    SysConfig sysConfig;

    @Resource
    OSSConfig ossConfig;

    @Resource
    ICBCFeignClient icbcFeignClient;

    @Resource
    ICBCFeignNormal icbcFeignFileDownLoad;

    @Resource
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Resource
    private ViolationUtil violationUtil;

    @Resource
    private BankCreditInfoDOMapper bankCreditInfoDOMapper;

    @Resource
    private LoanCreditInfoDOMapper loanCreditInfoDOMapper;

    @Resource
    private LoanProcessService loanProcessService;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private BankInterfaceFileSerialDOMapper bankInterfaceFileSerialDOMapper;

    @Resource
    private LoanCreditInfoService loanCreditInfoService;


    @Override
    public String fileDownload(String filesrc, String fileType) {

        String returnKey = null;
        try {
            boolean filedownload = icbcFeignFileDownLoad.filedownload(filesrc, fileType);
            String serverRecvPath = sysConfig.getServerRecvPath();
            serverRecvPath = serverRecvPath.replaceAll("FILETYPE", fileType);
            String fileAndPath = FtpUtil.icbcDownload(serverRecvPath, DateUtil.getDate() + "_" + fileType + ".txt");
            OSSClient ossClient = OSSUnit.getOSSClient();
            String diskName = ossConfig.getDownLoadDiskName();
            File file = new File(fileAndPath);
            OSSUnit.deleteFile(ossClient, ossConfig.getBucketName(), ossConfig.getDownLoadDiskName() + File.separator, file.getName());
            OSSUnit.uploadObject2OSS(ossClient, file, ossConfig.getBucketName(), ossConfig.getDownLoadDiskName() + File.separator);
            returnKey = diskName + File.separator + file.getName();
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }

        return returnKey;
    }

    @Override
    public void applyCreditCallback(ICBCApiCallbackParam.ApplyCreditCallback applyCreditCallback) {

        logger.info("征信查询回调===============================================================");
        violationUtil.violation(applyCreditCallback);
        //只有在非成功状态和退回的流水可以进行更新
        if (!checkStatus(applyCreditCallback.getPub().getCmpseq())) {
            return;
        }

        BankInterfaceSerialDO D = bankInterfaceSerialDOMapper.selectByPrimaryKey(applyCreditCallback.getPub().getCmpseq());
        if (D == null) {
            return;
        }

        if (!sysConfig.getAssurerno().equals(applyCreditCallback.getPub().getAssurerno())) {
            throw new BizException("保单号错误");
        }

        if (!sysConfig.getPlatno().equals(applyCreditCallback.getPub().getPlatno())) {
            throw new BizException("平台编号错误");
        }


        /*
        001:通过；
        003:不通过；
        099:退回，由于资料不全等原因退回
        */

        logger.info("征信查询回调 状态 ===============================================================" + applyCreditCallback.getPub().getCmpseq() + "：" + applyCreditCallback.getReq().getResult());
        BankInterfaceSerialDO bankInterfaceSerialDO = new BankInterfaceSerialDO();
        bankInterfaceSerialDO.setSerialNo(applyCreditCallback.getPub().getCmpseq());
        if (IDict.K_RESULT.PASS.equals(applyCreditCallback.getReq().getResult())) {

            bankInterfaceSerialDO.setStatus(IDict.K_JYZT.SUCCESS);

            LoanCreditInfoDO up = new LoanCreditInfoDO();
            up.setCustomerId(D.getCustomerId());
            up.setType(CREDIT_TYPE_BANK);
            if (bankNoByCusId(D.getCustomerId())) {
                up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
            }
            loanCreditInfoService.save(up);

//            List<LoanCreditInfoDO> loanCreditInfoDOS = loanCreditInfoDOMapper.getByCustomerIdAndType(D.getCustomerId(), new Byte("1"));
//
//            if (CollectionUtils.isEmpty(loanCreditInfoDOS)) {
//                LoanCreditInfoDO up = new LoanCreditInfoDO();
//                up.setCustomerId(D.getCustomerId());
//                if (bankNoByCusId(D.getCustomerId())) {
//                    up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
//                }
//                up.setType(new Byte("1"));
//                up.setStatus(new Byte("0"));
//                up.setGmtCreate(new Date());
//                loanCreditInfoDOMapper.insertSelective(up);
//            } else {
//                LoanCreditInfoDO up = loanCreditInfoDOS.get(0);
//                up.setCustomerId(D.getCustomerId());
//                if (bankNoByCusId(D.getCustomerId())) {
//                    up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
//                }
//                up.setGmtModify(new Date());
//                loanCreditInfoDOMapper.updateByPrimaryKeySelective(up);
//            }

        } else if (IDict.K_RESULT.NOPASS.equals(applyCreditCallback.getReq().getResult())) {

            bankInterfaceSerialDO.setStatus(IDict.K_JYZT.SUCCESS_ERROR);

            LoanCreditInfoDO up = new LoanCreditInfoDO();
            up.setCustomerId(D.getCustomerId());
            up.setType(CREDIT_TYPE_BANK);
            if (bankNoByCusId(D.getCustomerId())) {
                up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
            }
            loanCreditInfoService.save(up);

//            List<LoanCreditInfoDO> loanCreditInfoDOS = loanCreditInfoDOMapper.getByCustomerIdAndType(D.getCustomerId(), new Byte("1"));
//
//            if (CollectionUtils.isEmpty(loanCreditInfoDOS)) {
//                LoanCreditInfoDO up = new LoanCreditInfoDO();
//                up.setCustomerId(D.getCustomerId());
//                if (bankNoByCusId(D.getCustomerId())) {
//                    up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
//                }
//                up.setType(new Byte("1"));
//                up.setStatus(new Byte("0"));
//                up.setGmtCreate(new Date());
//                loanCreditInfoDOMapper.insertSelective(up);
//            } else {
//                LoanCreditInfoDO up = loanCreditInfoDOS.get(0);
//                up.setCustomerId(D.getCustomerId());
//                if (bankNoByCusId(D.getCustomerId())) {
//                    up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
//                }
//                up.setGmtModify(new Date());
//                loanCreditInfoDOMapper.updateByPrimaryKeySelective(up);
//            }

//
//            logger.info("征信查询回调 自动打回开始 ==============================================================="+applyCreditCallback.getPub().getCmpseq()+"："+applyCreditCallback.getReq().getResult());
//            try {
//                ApprovalParam approvalParam = new ApprovalParam();
//                approvalParam.setAction(new Byte("0"));
//                approvalParam.setOrderId(Long.parseLong(applyCreditCallback.getPub().getOrderno()));
//                approvalParam.setTaskDefinitionKey("usertask_bank_credit_record");
//                approvalParam.setNeedLog(false);
//                approvalParam.setCheckPermission(false);
//                approvalParam.setInfo(applyCreditCallback.getReq().getNote());
//                loanProcessService.approval(approvalParam);
//            }catch (Exception e){
//                logger.info("征信查询回调打回异常");
//            }

//            logger.info("征信查询回调 自动打回成功 ==============================================================="+applyCreditCallback.getPub().getCmpseq()+"："+applyCreditCallback.getReq().getResult());


        } else if (IDict.K_RESULT.BACK.equals(applyCreditCallback.getReq().getResult())) {

            bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.BACK));
            bankInterfaceSerialDO.setRejectReason(applyCreditCallback.getReq().getNote());

            LoanCreditInfoDO up = new LoanCreditInfoDO();
            up.setCustomerId(D.getCustomerId());
            up.setType(CREDIT_TYPE_BANK);
            if (bankNoByCusId(D.getCustomerId())) {
                up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
            }
            loanCreditInfoService.save(up);


//            List<LoanCreditInfoDO> loanCreditInfoDOS = loanCreditInfoDOMapper.getByCustomerIdAndType(D.getCustomerId(), new Byte("1"));
//
//            if (CollectionUtils.isEmpty(loanCreditInfoDOS)) {
//                LoanCreditInfoDO up = new LoanCreditInfoDO();
//                up.setCustomerId(D.getCustomerId());
//                if (bankNoByCusId(D.getCustomerId())) {
//                    up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
//                }
//                up.setType(new Byte("1"));
//                up.setStatus(new Byte("0"));
//                up.setGmtCreate(new Date());
//                loanCreditInfoDOMapper.insertSelective(up);
//            } else {
//                LoanCreditInfoDO up = loanCreditInfoDOS.get(0);
//                up.setCustomerId(D.getCustomerId());
//                if (bankNoByCusId(D.getCustomerId())) {
//                    up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
//                }
//                up.setGmtModify(new Date());
//                loanCreditInfoDOMapper.updateByPrimaryKeySelective(up);
//            }

//            logger.info("征信查询回调 自动打回开始 ==============================================================="+applyCreditCallback.getPub().getCmpseq()+"："+applyCreditCallback.getReq().getResult());

//            try {
//                ApprovalParam approvalParam = new ApprovalParam();
//                approvalParam.setAction(new Byte("0"));
//                approvalParam.setOrderId(Long.parseLong(applyCreditCallback.getPub().getOrderno()));
//                approvalParam.setTaskDefinitionKey("usertask_bank_credit_record");
//                approvalParam.setNeedLog(false);
//                approvalParam.setCheckPermission(false);
//                approvalParam.setInfo(applyCreditCallback.getReq().getNote());
//                loanProcessService.approval(approvalParam);
//            }catch (Exception e){
//                logger.info("征信查询回调打回异常");
//            }
//            logger.info("征信查询回调 自动打回成功 ==============================================================="+applyCreditCallback.getPub().getCmpseq()+"："+applyCreditCallback.getReq().getResult());
        } else {
            throw new BizException("未知错误");
        }
        bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
        logger.info("存储数据 开始 ===============================================================");

        BankCreditInfoDO DO = bankCreditInfoDOMapper.selectByPrimaryKey(applyCreditCallback.getPub().getCmpseq());
        BankCreditInfoDO V = new BankCreditInfoDO();
        V.setSerialNo(applyCreditCallback.getPub().getCmpseq());
        V.setCustomerId(D.getCustomerId());
        V.setCustname(applyCreditCallback.getReq().getCustname());
        V.setIdno(applyCreditCallback.getReq().getIdno());
        V.setRelation(applyCreditCallback.getReq().getRelation());
        V.setResult(applyCreditCallback.getReq().getResult());
        V.setLoancrdt(applyCreditCallback.getReq().getLoanCrdt());
        V.setCardcrdt(applyCreditCallback.getReq().getCardCrdt());
        V.setLeftnum(applyCreditCallback.getReq().getLeftNum());
        V.setLeftamount(applyCreditCallback.getReq().getLeftAmount());
        V.setNote(applyCreditCallback.getReq().getNote());

        if (DO == null) {
            bankCreditInfoDOMapper.insertSelective(V);
        } else {
            bankCreditInfoDOMapper.updateByPrimaryKeySelective(V);
        }

        logger.info("存储数据 结束  ===============================================================");
    }

    public boolean bankNoByCusId(Long cusId) {
        boolean flag = false;
        String bankName = loanCreditInfoDOMapper.bankNoByCusId(cusId);
        if (!"中国工商银行杭州城站支行".equals(bankName)) {
            flag = true;
        }
        return flag;
    }

    @Override
    public void applyDiviGeneralCallback(ICBCApiCallbackParam.ApplyDiviGeneralCallback applyDiviGeneralCallback) {
        logger.info("分期退回回调开始===============================================================");
        violationUtil.violation(applyDiviGeneralCallback);
        if (!checkStatus(applyDiviGeneralCallback.getPub().getCmpseq())) {
            return;
        }


        if (!sysConfig.getAssurerno().equals(applyDiviGeneralCallback.getPub().getAssurerno())) {
            throw new BizException("保单号错误");
        }

        if (!sysConfig.getPlatno().equals(applyDiviGeneralCallback.getPub().getPlatno())) {
            throw new BizException("平台编号错误");
        }


        BankInterfaceSerialDO bankInterfaceSerialDO = new BankInterfaceSerialDO();
        bankInterfaceSerialDO.setSerialNo(applyDiviGeneralCallback.getPub().getCmpseq());
        bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.BACK));
        bankInterfaceSerialDO.setRejectReason(applyDiviGeneralCallback.getReq().getBacknote());
        bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
        logger.info("分期退回回调结束===============================================================");
    }

    @Override
    public void multimediaUploadCallback(ICBCApiCallbackParam.MultimediaUploadCallback multimediaUploadCallback) {
        logger.info("多媒体退回回调开始===============================================================");
        violationUtil.violation(multimediaUploadCallback);
        if (!checkStatus(multimediaUploadCallback.getPub().getCmpseq())) {
            return;
        }

        if (!sysConfig.getAssurerno().equals(multimediaUploadCallback.getPub().getAssurerno())) {
            throw new BizException("保单号错误");
        }

        if (!sysConfig.getPlatno().equals(multimediaUploadCallback.getPub().getPlatno())) {
            throw new BizException("平台编号错误");
        }

        BankInterfaceSerialDO bankInterfaceSerialDO = new BankInterfaceSerialDO();
        bankInterfaceSerialDO.setSerialNo(multimediaUploadCallback.getPub().getCmpseq());
        bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.BACK));
        bankInterfaceSerialDO.setRejectReason(multimediaUploadCallback.getReq().getBacknote());
        bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
        logger.info("多媒体退回回调结束===============================================================");
    }

    @Override
    public void creditCardApplyCallback(ICBCApiCallbackParam.CreditCardApplyCallback creditCardApplyCallback) {

        logger.info("开卡退回回调开始===============================================================");
        violationUtil.violation(creditCardApplyCallback);
        //只有在非成功状态和退回的流水可以进行更新
        if (!checkStatus(creditCardApplyCallback.getPub().getCmpseq())) {
            return;
        }

        BankInterfaceSerialDO D = bankInterfaceSerialDOMapper.selectByPrimaryKey(creditCardApplyCallback.getPub().getCmpseq());
        if (D == null) {
            return;
        }

        if (!sysConfig.getAssurerno().equals(creditCardApplyCallback.getPub().getAssurerno())) {
            throw new BizException("保单号错误");
        }

        if (!sysConfig.getPlatno().equals(creditCardApplyCallback.getPub().getPlatno())) {
            throw new BizException("平台编号错误");
        }

        BankInterfaceSerialDO bankInterfaceSerialDO = new BankInterfaceSerialDO();
        bankInterfaceSerialDO.setSerialNo(creditCardApplyCallback.getPub().getCmpseq());
        bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.BACK));
        bankInterfaceSerialDO.setRejectReason(creditCardApplyCallback.getReq().getBacknote());
        bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
        logger.info("开卡退回回调结束===============================================================");
    }

    @Override
    public ICBCApiCallbackParam.Ans artificialgainImage(ICBCApiCallbackParam.ArtificialGainImageCallback artificialGainImageCallback) {
        logger.info("手动获取图片回调开始===============================================================");
        violationUtil.violation(artificialGainImageCallback);
        if (!checkStatus(artificialGainImageCallback.getPub().getCmpseq())) {
            throw new BizException("缺少流水号");
        }

        if (TermFileEnum.getKeyByValue(artificialGainImageCallback.getReq().getPicid()) == null) {
            throw new BizException("id 不存在");
        }

        List<UniversalBankInterfaceFileSerialDO> list = loanQueryDOMapper.selectSuccessBankInterfaceFileSerialBySeriesNoAndFileType(artificialGainImageCallback.getPub().getCmpseq(), artificialGainImageCallback.getReq().getPicid());

        if (CollectionUtils.isEmpty(list)) {
            throw new BizException("图片不存在");
        }

        List<ICBCApiCallbackParam.Pic> picList = Lists.newArrayList();

        for (UniversalBankInterfaceFileSerialDO universalBankInterfaceFileSerialDO : list) {
            ICBCApiCallbackParam.Pic pic = new ICBCApiCallbackParam.Pic();
            String note = "未知";
            if (TermFileEnum.getKeyByValue(universalBankInterfaceFileSerialDO.getFile_type()) != null) {
                note = LoanFileEnum.getNameByCode(TermFileEnum.getKeyByValue(universalBankInterfaceFileSerialDO.getFile_type()));
            }
            pic.setPicnote(note);

            if (StringUtils.isNotBlank(universalBankInterfaceFileSerialDO.getFile_name())) {

                String date = universalBankInterfaceFileSerialDO.getFile_name().substring(0, 8);
                String path = "http://109.2.148.206:9030/ftpwebv3/yunche/" + date + "/" + universalBankInterfaceFileSerialDO.getFile_name();
                pic.setPicurl(path);
            }
            picList.add(pic);
        }
        ICBCApiCallbackParam.Ans ans = new ICBCApiCallbackParam.Ans();
        ans.setPics(picList);
        ans.setPicnum(String.valueOf(list.size()));
        return ans;
    }

    private boolean checkStatus(String cmpseq) {

        BankInterfaceSerialDO D = bankInterfaceSerialDOMapper.selectByPrimaryKey(cmpseq);
        if (D == null) {
            return false;
        }

        if (StringUtils.isBlank(cmpseq)) {
            throw new BizException("缺少流水号");
        }
        BankInterfaceSerialDO V = bankInterfaceSerialDOMapper.selectByPrimaryKey(cmpseq);
        if (V == null) {
            return false;
        }
        if (V.getApiStatus() == null) {
            return false;
        }
        if (V.getApiStatus().intValue() != 200) {
            return false;
        }

        //只有处理中和超时状态才能进行后续处理
        if (!IDict.K_JYZT.PROCESS.equals(V.getStatus())) {
            return false;
        }

        return true;
    }
}
