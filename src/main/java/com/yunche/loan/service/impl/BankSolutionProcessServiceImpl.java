package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.CreditEnum;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.feign.client.ICBCFeignNormal;
import com.yunche.loan.config.util.FtpUtil;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.ViolationUtil;
import com.yunche.loan.domain.entity.BankCreditInfoDO;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.entity.LoanCreditInfoDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.ICBCApiCallbackParam;
import com.yunche.loan.mapper.BankCreditInfoDOMapper;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.LoanCreditInfoDOMapper;
import com.yunche.loan.service.BankSolutionProcessService;
import com.yunche.loan.service.LoanProcessService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

@Service
public class BankSolutionProcessServiceImpl implements BankSolutionProcessService{
    private static final Logger logger = LoggerFactory.getLogger(BankSolutionProcessServiceImpl.class);
    @Autowired
    SysConfig sysConfig;

    @Autowired
    OSSConfig ossConfig;

    @Autowired
    ICBCFeignClient icbcFeignClient;

    @Autowired
    ICBCFeignNormal icbcFeignFileDownLoad;

    @Autowired
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Resource
    private ViolationUtil violationUtil;

    @Resource
    private BankCreditInfoDOMapper bankCreditInfoDOMapper;

    @Resource
    private LoanCreditInfoDOMapper loanCreditInfoDOMapper;

    @Resource
    private LoanProcessService loanProcessService;

    @Override
    public String  fileDownload(String filesrc) {

        String returnKey=null;
        try {
            boolean filedownload = icbcFeignFileDownLoad.filedownload(filesrc);
            String fileAndPath = FtpUtil.icbcDownload(sysConfig.getFileServerpath() + filesrc);
            OSSClient ossClient = OSSUnit.getOSSClient();
            String diskName = ossConfig.getDownLoadDiskName();
            File file = new File(fileAndPath);
            OSSUnit.deleteFile(ossClient,ossConfig.getBucketName(),ossConfig.getDownLoadDiskName()+File.separator,file.getName());
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
        if(!checkStatus(applyCreditCallback.getPub().getCmpseq())){
            return;
        }

        BankInterfaceSerialDO D = bankInterfaceSerialDOMapper.selectByPrimaryKey(applyCreditCallback.getPub().getCmpseq());
        if(D == null){
            return;
        }

        if(sysConfig.getAssurerno().equals(applyCreditCallback.getPub().getAssurerno())){
            throw new BizException("保单号错误");
        }

        if(sysConfig.getPlatno().equals(applyCreditCallback.getPub().getPlatno())){
            throw new BizException("平台编号错误");
        }


        /*
        001:通过；
        003:不通过；
        099:退回，由于资料不全等原因退回
        */

        logger.info("征信查询回调 状态 ==============================================================="+applyCreditCallback.getPub().getCmpseq()+"："+applyCreditCallback.getReq().getResult());
        BankInterfaceSerialDO bankInterfaceSerialDO = new BankInterfaceSerialDO();
        bankInterfaceSerialDO.setSerialNo(applyCreditCallback.getPub().getCmpseq());
        if(IDict.K_RESULT.PASS.equals(applyCreditCallback.getReq().getResult())){
            bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.SUCCESS));
            List<LoanCreditInfoDO>  loanCreditInfoDOS =  loanCreditInfoDOMapper.getByCustomerIdAndType(D.getCustomerId(),new Byte("1"));

            if(CollectionUtils.isEmpty(loanCreditInfoDOS)){
                LoanCreditInfoDO up = new LoanCreditInfoDO();
                up.setCustomerId(D.getCustomerId());
                up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
                up.setInfo(applyCreditCallback.getReq().getNote());
                up.setType(new Byte("1"));
                up.setStatus(new Byte("0"));
                up.setGmtCreate(new Date());
                loanCreditInfoDOMapper.insertSelective(up);
            }else {
                LoanCreditInfoDO up = loanCreditInfoDOS.get(0);
                up.setCustomerId(D.getCustomerId());
                up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
                up.setInfo(applyCreditCallback.getReq().getNote());
                up.setGmtModify(new Date());
                loanCreditInfoDOMapper.updateByPrimaryKeySelective(up);
            }
        }else if(IDict.K_RESULT.NOPASS.equals(applyCreditCallback.getReq().getResult())){
            bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.SUCCESS_ERROR));
            bankInterfaceSerialDO.setRejectReason(applyCreditCallback.getReq().getNote());

            List<LoanCreditInfoDO>  loanCreditInfoDOS =  loanCreditInfoDOMapper.getByCustomerIdAndType(D.getCustomerId(),new Byte("1"));

            if(CollectionUtils.isEmpty(loanCreditInfoDOS)){
                LoanCreditInfoDO up = new LoanCreditInfoDO();
                up.setCustomerId(D.getCustomerId());
                up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
                up.setInfo(applyCreditCallback.getReq().getNote());
                up.setType(new Byte("1"));
                up.setStatus(new Byte("0"));
                up.setGmtCreate(new Date());
                loanCreditInfoDOMapper.insertSelective(up);
            }else {
                LoanCreditInfoDO up = loanCreditInfoDOS.get(0);
                up.setCustomerId(D.getCustomerId());
                up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
                up.setInfo(applyCreditCallback.getReq().getNote());
                up.setGmtModify(new Date());
                loanCreditInfoDOMapper.updateByPrimaryKeySelective(up);
            }


        }else if(IDict.K_RESULT.BACK.equals(applyCreditCallback.getReq().getResult())){
            bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.BACK));
            bankInterfaceSerialDO.setRejectReason(applyCreditCallback.getReq().getNote());

            List<LoanCreditInfoDO>  loanCreditInfoDOS =  loanCreditInfoDOMapper.getByCustomerIdAndType(D.getCustomerId(),new Byte("1"));

            if(CollectionUtils.isEmpty(loanCreditInfoDOS)){
                LoanCreditInfoDO up = new LoanCreditInfoDO();
                up.setCustomerId(D.getCustomerId());
                up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
                up.setInfo(applyCreditCallback.getReq().getNote());
                up.setType(new Byte("1"));
                up.setStatus(new Byte("0"));
                up.setGmtCreate(new Date());
                loanCreditInfoDOMapper.insertSelective(up);
            }else {
                LoanCreditInfoDO up = loanCreditInfoDOS.get(0);
                up.setCustomerId(D.getCustomerId());
                up.setResult(CreditEnum.getValueByKey(applyCreditCallback.getReq().getResult()));
                up.setInfo(applyCreditCallback.getReq().getNote());
                up.setGmtModify(new Date());
                loanCreditInfoDOMapper.updateByPrimaryKeySelective(up);
            }
            logger.info("征信查询回调 自动打回开始 ==============================================================="+applyCreditCallback.getPub().getCmpseq()+"："+applyCreditCallback.getReq().getResult());

            ApprovalParam approvalParam = new ApprovalParam();
            approvalParam.setAction(new Byte("0"));
            approvalParam.setInfo(applyCreditCallback.getReq().getNote());
            approvalParam.setOrderId(Long.parseLong(applyCreditCallback.getPub().getOrderno()));
            approvalParam.setTaskDefinitionKey("usertask_bank_credit_record");

            loanProcessService.approval(approvalParam);

            logger.info("征信查询回调 自动打回成功 ==============================================================="+applyCreditCallback.getPub().getCmpseq()+"："+applyCreditCallback.getReq().getResult());
        }else{
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

        if(DO == null){
            bankCreditInfoDOMapper.insertSelective(V);
        }else{
            bankCreditInfoDOMapper.updateByPrimaryKeySelective(V);
        }

        logger.info("存储数据 结束  ===============================================================");
    }

    @Override
    public void applyDiviGeneralCallback(ICBCApiCallbackParam.ApplyDiviGeneralCallback applyDiviGeneralCallback) {
        violationUtil.violation(applyDiviGeneralCallback);
        if(!checkStatus(applyDiviGeneralCallback.getPub().getCmpseq())){
            return;
        }


        if(sysConfig.getAssurerno().equals(applyDiviGeneralCallback.getPub().getAssurerno())){
            throw new BizException("保单号错误");
        }

        if(sysConfig.getPlatno().equals(applyDiviGeneralCallback.getPub().getPlatno())){
            throw new BizException("平台编号错误");
        }


        BankInterfaceSerialDO bankInterfaceSerialDO = new BankInterfaceSerialDO();
        bankInterfaceSerialDO.setSerialNo(applyDiviGeneralCallback.getPub().getCmpseq());
        bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.BACK));
        bankInterfaceSerialDO.setRejectReason(applyDiviGeneralCallback.getReq().getBacknote());
        bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
    }

    @Override
    public void multimediaUploadCallback(ICBCApiCallbackParam.MultimediaUploadCallback multimediaUploadCallback) {
        violationUtil.violation(multimediaUploadCallback);
        if(!checkStatus(multimediaUploadCallback.getPub().getCmpseq())){
            return;
        }

        if(sysConfig.getAssurerno().equals(multimediaUploadCallback.getPub().getAssurerno())){
            throw new BizException("保单号错误");
        }

        if(sysConfig.getPlatno().equals(multimediaUploadCallback.getPub().getPlatno())){
            throw new BizException("平台编号错误");
        }

        BankInterfaceSerialDO bankInterfaceSerialDO = new BankInterfaceSerialDO();
        bankInterfaceSerialDO.setSerialNo(multimediaUploadCallback.getPub().getCmpseq());
        bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.BACK));
        bankInterfaceSerialDO.setRejectReason(multimediaUploadCallback.getReq().getBacknote());
        bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
    }

    private boolean checkStatus(String cmpseq){
        if(StringUtils.isBlank(cmpseq)){
            throw new BizException("缺少流水号");
        }
        BankInterfaceSerialDO V = bankInterfaceSerialDOMapper.selectByPrimaryKey(cmpseq);
        if(V == null){
            return false;
        }
        if(V.getApiStatus() == null){
            return false;
        }
        if(V.getApiStatus().intValue() == 200){
            return false;
        }

        //只有处理中和超时状态才能进行后续处理
        if(!IDict.K_JYZT.PROCESS.equals(V.getStatus()) && !IDict.K_JYZT.TIMEOUT.equals(V.getStatus()) && !IDict.K_JYZT.FAIL.equals(V.getStatus()) && !IDict.K_JYZT.SUCCESS_ERROR.equals(V.getStatus())){
            return false;
        }

        return true;
    }
}
