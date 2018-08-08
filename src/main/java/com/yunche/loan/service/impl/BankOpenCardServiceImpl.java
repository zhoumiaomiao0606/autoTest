package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.*;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.feign.response.ApplycreditstatusResponse;
import com.yunche.loan.config.feign.response.CreditCardApplyResponse;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.*;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.*;
import static com.yunche.loan.config.constant.LoanCustomerConst.CUST_TYPE_EMERGENCY_CONTACT;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanFileEnum.*;
import static com.yunche.loan.config.thread.ThreadPool.executorService;

@Service
public class BankOpenCardServiceImpl implements BankOpenCardService {

    private static final Logger LOG = LoggerFactory.getLogger(BankOpenCardServiceImpl.class);


    @Autowired
    LoanQueryService loanQueryService;

    @Autowired
    LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Autowired
    BankSolutionService bankSolutionService;

    @Autowired
    LoanFileDOMapper loanFileDOMapper;

    @Autowired
    MaterialDownHisDOMapper materialDownHisDOMapper;

    @Autowired
    BankFileListDOMapper bankFileListDOMapper;

    @Autowired
    BankFileListRecordDOMapper bankFileListRecordDOMapper;

    @Autowired
    AsyncUpload asyncUpload;

    @Autowired
    LoanBaseInfoService loanBaseInfoService;

    @Autowired
    LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    SysConfig sysConfig;

    @Autowired
    LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    BankDOMapper bankDOMapper;

    @Autowired
    BankInterfaceFileSerialDOMapper bankInterfaceFileSerialDOMapper;

    @Autowired
    LoanProcessDOMapper loanProcessDOMapper;

    @Autowired
    LoanFileService loanFileService;

    @Autowired
    LoanProcessService loanProcessService;

    /**
     * 银行开卡详情页
     *
     * @param orderId
     * @return
     */
    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {
        RecombinationVO recombinationVO = new RecombinationVO();
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Long customerId = loanOrderDO.getLoanCustomerId();

        UniversalCustomerDetailVO universalCustomerDetailVO = loanQueryDOMapper.selectUniversalCustomerDetail(orderId, customerId);
        BankInterfaceSerialVO bankInterfaceSerialVO = new BankInterfaceSerialVO();
        BankInterfaceSerialDO serialDO = bankInterfaceSerialDOMapper.selectByCustomerIdAndTransCode(customerId, IDict.K_TRANS_CODE.CREDITCARDAPPLY);
//        if(serialDO!=null) {
//            BeanUtils.copyProperties(serialDO, bankInterfaceSerialVO);
//            BankInterfaceFileSerialDO bankInterfaceFileSerialDO = bankInterfaceFileSerialDOMapper.selectByPrimaryKey(Long.valueOf(serialDO.getSerialNo()));
//            if (bankInterfaceFileSerialDO != null) {
//                if (bankInterfaceFileSerialDO.getSuccess().equals(IDict.K_YORN.K_YORN_NO) && bankInterfaceFileSerialDO.getError().equals((byte) 2)) {
//                    bankInterfaceSerialVO.setMergeStatus(String.valueOf(IDict.K_YORN.K_YORN_NO));
//                } else {
//                    bankInterfaceSerialVO.setMergeStatus(String.valueOf(IDict.K_YORN.K_YORN_YES));
//                }
//            }
//        }

        if (null != serialDO) {
            BeanUtils.copyProperties(serialDO, bankInterfaceSerialVO);
        }

        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO, "流程不存在");
        Byte telephoneVerify = loanProcessDO.getTelephoneVerify();
        switch (telephoneVerify) {
            case 0:
                bankInterfaceSerialVO.setElectricResults("未执行到此节点");
                break;
            case 1:
                bankInterfaceSerialVO.setElectricResults("已处理");
                break;
            case 2:
                bankInterfaceSerialVO.setElectricResults("未处理");
                break;
            case 3:
                bankInterfaceSerialVO.setElectricResults("打回修改");
                break;
            default:
                bankInterfaceSerialVO.setElectricResults("未知");
        }

        ResultBean<LoanBaseInfoVO> loanBaseInfoVOResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
        //贷款信息
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);
        Set<Byte> bytes = Sets.newHashSet(CUST_TYPE_EMERGENCY_CONTACT);
        List<LoanCustomerDO> emergencyContact = loanCustomerDOMapper.selectSelfAndRelevanceCustomersByCustTypes(orderId, bytes);


        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        recombinationVO.setFinancial(financialSchemeVO);
        recombinationVO.setLoanBaseInfo(loanBaseInfoVOResultBean.getData());
        recombinationVO.setInfo(universalCustomerDetailVO);
        recombinationVO.setBankSerial(bankInterfaceSerialVO);
        recombinationVO.setEmergencyContacts(emergencyContact);
        recombinationVO.setCustomers(customers);
        return ResultBean.ofSuccess(recombinationVO);
    }

    /**
     * 银行开卡
     *
     * @param orderId
     * @return
     */
    @Override
    public ResultBean openCard(Long orderId) {
        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(orderDO, "订单不存在");
        Long customerId = orderDO.getLoanCustomerId();
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(customerId, VALID_STATUS);

        loanQueryService.checkBankInterFaceSerialStatus(customerId, IDict.K_TRANS_CODE.CREDITCARDAPPLY);

//        boolean flag = bankInterfaceSerialDOMapper.checkRequestBussIsSucessByTransCodeOrderId(customerId, IDict.K_TRANS_CODE.CREDITCARDAPPLY);
//        if(flag){
//            throw new BizException(loanCustomerDO.getName()+":开卡处理中,请勿重复开卡...");
//        }

        BankOpenCardParam bankOpenCardParam = new BankOpenCardParam();

        bankOpenCardParam.setCustomerId(orderDO.getLoanCustomerId().toString());

        bankOpenCardParam.setOrderId(orderId);
        // 文件合并上传
        mergeUpload(bankOpenCardParam);

        CreditCardApplyResponse creditcardapply = bankSolutionService.creditcardapply(bankOpenCardParam);

        return ResultBean.ofSuccess(creditcardapply);
    }

    /**
     * 导入银行开卡文件记录
     *
     * @param ossKey
     * @return
     */
    @Override
    @Transactional
    public boolean importFile(String ossKey) {

        try {
            InputStream in = OSSUnit.getOSS2InputStream(ossKey);
            InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
            BufferedReader bufReader = new BufferedReader(inReader);

            BankFileListDO bankFileListDO = new BankFileListDO();
            String[] split1 = ossKey.split(File.separator);
            String fileName = ossKey;
            if (split1.length > 0) {
                fileName = split1[split1.length - 1].trim();
            }
            bankFileListDO.setFileName(fileName);
            bankFileListDO.setFileKey(ossKey);
            bankFileListDO.setFileType(IDict.K_WJLX.WJLX_0);
            bankFileListDO.setGmtCreate(new Date());
            bankFileListDO.setOperator("auto");
            int bankFileListId = bankFileListDOMapper.insertSelective(bankFileListDO);

            String line = "";
            BankFileListRecordDOKey bankFileListRecordDOKey = new BankFileListRecordDOKey();

            bankFileListRecordDOMapper.deleteBylistId(Long.valueOf(bankFileListDO.getId()));
            /**
             * 地区号、平台编号、担保单位编号、订单号、开卡日期、卡号、姓名、证件类型、
             * 证件号码、发卡标志[0：开卡失败 1：开卡成功]、对账单日、还款日
             */
            List<BankFileListRecordDO> recordLists = Lists.newArrayList();
            while ((line = bufReader.readLine()) != null) {
                String[] split = line.split("\\|");
                if (split.length >= 12) {
                    BankFileListRecordDO bankFileListRecordDO = packObject(split);
                    bankFileListRecordDO.setBankFileListId(Long.valueOf(bankFileListId));
                    recordLists.add(bankFileListRecordDO);
                }

            }
            List<BankFileListRecordDO> list = recordLists.parallelStream().filter(e -> e.getIsCustomer().equals(K_YORN_YES)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(list)) {
                int count = bankFileListRecordDOMapper.insertBatch(list);
                Preconditions.checkArgument(count == list.size(), "批量插入失败");
            }
            list.stream().filter(Objects::nonNull).forEach(e->{
                LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(e.getOrderId());
                LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), BaseConst.VALID_STATUS);
                loanCustomerDO.setLendCard(e.getCardNumber());
                loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
            });
            list.parallelStream().filter(Objects::nonNull).forEach(e -> {

                LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(e.getOrderId());
                Byte openCard = loanProcessDO.getBankOpenCard();
                if (!openCard.equals(LoanOrderProcessConst.TASK_PROCESS_DONE)) {
                    ApprovalParam approvalParam = new ApprovalParam();
                    approvalParam.setOrderId(e.getOrderId());
                    approvalParam.setTaskDefinitionKey(LoanProcessEnum.BANK_OPEN_CARD.getCode());
                    approvalParam.setAction(LoanProcessConst.ACTION_PASS);
                    approvalParam.setNeedLog(false);
                    approvalParam.setCheckPermission(false);
                    ResultBean<Void> approvalResultBean = loanProcessService.approval(approvalParam);
                    LOG.info(e.getOrderId() + approvalResultBean.getMsg());
                }
            });
        } catch (UnsupportedEncodingException e) {
            throw new BizException("导入文件失败");
        } catch (IOException e) {
            throw new BizException("导入文件失败");
        }
        return true;
    }

    /**
     * 暂存开卡信息
     *
     * @param bankOpenCardParam
     * @return
     */
    @Override
    public ResultBean save(BankOpenCardParam bankOpenCardParam) {

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(bankOpenCardParam, loanCustomerDO);
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(bankOpenCardParam.getOrderId());
        Preconditions.checkNotNull(loanOrderDO, "订单不存在");
        Long loanCustomerId = loanOrderDO.getLoanCustomerId();
        loanCustomerDO.setId(loanCustomerId);
        Preconditions.checkNotNull(loanCustomerDO.getId(), "客户信息不存在");
        int count = loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "客户信息更新失败");
        ResultBean<Void> updateFileResult = loanFileService.updateOrInsertByCustomerIdAndUploadType(loanCustomerId, bankOpenCardParam.getFiles(), UPLOAD_TYPE_NORMAL);

        return ResultBean.ofSuccess("保存成功");
    }

    @Override
    public ResultBean taskschedule(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if (loanOrderDO == null) {
            throw new BizException("此订单不存在");
        }

        Long baseId = loanOrderDO.getLoanBaseInfoId();
        if (baseId == null) {
            throw new BizException("征信信息不存在");
        }

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseId);
        if (loanBaseInfoDO == null) {
            throw new BizException("征信信息不存在");
        }
        //征信银行
        Long bankId = bankDOMapper.selectIdByName(loanBaseInfoDO.getBank());
        if (bankId == null) {
            throw new BizException("贷款银行不存在");
        }
        String serialNo = GeneratorIDUtil.execute();
        ICBCApiRequest.Applycreditstatus applycreditstatus = new ICBCApiRequest.Applycreditstatus();
        applycreditstatus.setPlatno(sysConfig.getPlatno());
        applycreditstatus.setZoneno(String.valueOf(loanBaseInfoDO.getAreaId()).substring(0, 4));
        if (IDict.K_BANK.ICBC_HZCZ.equals(String.valueOf(bankId))) {
            applycreditstatus.setPhybrno(sysConfig.getHzphybrno());
        } else if (IDict.K_BANK.ICBC_TZLQ.equals(String.valueOf(bankId))) {
            applycreditstatus.setPhybrno(sysConfig.getTzphybrno());
        }

//        String serialNo = GeneratorIDUtil.execute();
        applycreditstatus.setOrderno(String.valueOf(loanOrderDO.getId()));
        applycreditstatus.setAssurerno(sysConfig.getAssurerno());
        applycreditstatus.setCmpdate(DateUtil.getDate());
        applycreditstatus.setCmptime(DateUtil.getTime());
        applycreditstatus.setCmpseq(serialNo);
        applycreditstatus.setFileNum(String.valueOf(0));
        applycreditstatus.setCustomerId(String.valueOf(loanOrderDO.getLoanCustomerId()));
        ApplycreditstatusResponse response = bankSolutionService.applycreditstatus(applycreditstatus);
        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        loanCustomerDO.setId(loanOrderDO.getLoanCustomerId());
        if (StringUtils.isNotBlank(response.getStatus())) {
            loanCustomerDO.setOpenCardCurrStatus(response.getStatus());
        } else {
            loanCustomerDO.setOpenCardCurrStatus("44");
        }
        loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
        BankInterfaceSerialDO bankInterfaceSerialDO = new BankInterfaceSerialDO();
        bankInterfaceSerialDO.setSerialNo(serialNo);
        bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JJSTS.SUCCESS));
        int count = bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
        Preconditions.checkArgument(count > 0, "查询开卡状态异常");
        return ResultBean.ofSuccess(response);
    }

    /**
     * @param split
     */
    private BankFileListRecordDO packObject(String[] split) {
        BankFileListRecordDO bankFileListRecordDO = new BankFileListRecordDO();

        String areaId = split[0].trim();//地区号
        String platNo = split[1].trim();//平台编号
        String guarantyUnit = split[2].trim();//担保单位编号
        String orderId = split[3].trim();//订单号
        String openCardDate = split[4].trim();//开卡日期
        String cardNumber = split[5].trim();//卡号
        String name = split[6].trim();//姓名
        String cardType = split[7].trim();//证件类型
        String credentialNo = split[8].trim();//证件号码
        String hairpinFlag = split[9].trim();//发卡标志
        String accountStatement = split[10].trim();//对账单日
        String repayDate = split[11].trim();//还款日
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(orderId));
        if (loanOrderDO == null) {
            bankFileListRecordDO.setIsCustomer(K_YORN_NO);
            return bankFileListRecordDO;
        } else {
            bankFileListRecordDO.setIsCustomer(K_YORN_YES);
            bankFileListRecordDO.setCustomerId(loanOrderDO.getLoanCustomerId());
        }

        bankFileListRecordDO.setAreaId(areaId);
        bankFileListRecordDO.setPlatNo(platNo);
        bankFileListRecordDO.setGuarantyUnit(guarantyUnit);
        bankFileListRecordDO.setOrderId(Long.valueOf(orderId));
        bankFileListRecordDO.setOpencardDate(DateUtil.getDate(openCardDate));
        bankFileListRecordDO.setCardNumber(cardNumber);
        bankFileListRecordDO.setName(name);
        bankFileListRecordDO.setCardType(cardType);
        bankFileListRecordDO.setCredentialNo(credentialNo);
        bankFileListRecordDO.setHairpinFlag(hairpinFlag);
        bankFileListRecordDO.setAccountStatement(accountStatement);
        bankFileListRecordDO.setRepayDate(repayDate);


        return bankFileListRecordDO;
    }


    /**
     * 合并资料并上传至中间服务器
     *
     * @param bankOpenCardParam
     * @return
     */
    private void mergeUpload(BankOpenCardParam bankOpenCardParam) {

        List<LoanFileDO> idCardFront = loanFileDOMapper.listByCustomerIdAndType(Long.parseLong(bankOpenCardParam.getCustomerId()), ID_CARD_FRONT.getType(), (byte) 1);
        List<LoanFileDO> idCardback = loanFileDOMapper.listByCustomerIdAndType(Long.parseLong(bankOpenCardParam.getCustomerId()), ID_CARD_BACK.getType(), (byte) 1);
        List<LoanFileDO> specialQuotaApply = loanFileDOMapper.listByCustomerIdAndType(Long.parseLong(bankOpenCardParam.getCustomerId()), SPECIAL_QUOTA_APPLY.getType(), (byte) 1);
        List<LoanFileDO> openCardData = loanFileDOMapper.listByCustomerIdAndType(Long.parseLong(bankOpenCardParam.getCustomerId()), OPEN_CARD_DATA.getType(), (byte) 1);
        //台州不需要合并身份证
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(bankOpenCardParam.getOrderId());
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        Long bankId = bankDOMapper.selectIdByName(loanBaseInfoDO.getBank());
        Preconditions.checkNotNull(bankId, "贷款银行不存在");
        List<LoanFileDO> openCardTypes = Lists.newArrayList();
        if (IDict.K_BANK.ICBC_HZCZ.equals(String.valueOf(bankId))) {
            openCardTypes.addAll(idCardFront);
            openCardTypes.addAll(idCardback);
        }
        openCardTypes.addAll(openCardData);
        bankOpenCardParam.setBankId(bankId);
        //【开卡】专项额度核定申请表
        List<String> keys = Lists.newArrayList();
        specialQuotaApply.stream().filter(Objects::nonNull).filter(e -> StringUtils.isNotBlank(e.getPath())).forEach(e -> {
            String path = e.getPath();
            List<String> list = JSONArray.parseArray(path, String.class);
            keys.addAll(list);
        });
        Preconditions.checkArgument(keys.size() > 0, "专项额度核定申请表,不存在");
        String picName = GeneratorIDUtil.execute() + ImageUtil.PIC_SUFFIX;
        String serNo = GeneratorIDUtil.execute();
        String fileName = picName;

        ICBCApiRequest.Picture picture1 = new ICBCApiRequest.Picture();
        picture1.setPicid(IDict.K_PIC_ID.SPECIAL_QUOTA_APPLY);
        picture1.setPicname(fileName);
        picture1.setPicKeyList(keys);

        //开卡】开卡申请表(和身份证正反面合并成一张图片)
        List<String> openCardTypesStr = Lists.newArrayList();
        openCardTypes.stream().filter(e -> StringUtils.isNotBlank(e.getPath())).forEach(e -> {
            String path = e.getPath();
            List<String> list = JSONArray.parseArray(path, String.class);
            openCardTypesStr.addAll(list);
        });
        Preconditions.checkArgument(openCardTypesStr.size() > 0, "开卡申请表(和身份证正反面合并成一张图片)");
        String fileName2 = GeneratorIDUtil.execute() + ImageUtil.PIC_SUFFIX;
        ICBCApiRequest.Picture picture2 = new ICBCApiRequest.Picture();
        picture2.setPicid(IDict.K_PIC_ID.OPEN_CARD_DATA);
        picture2.setPicname(fileName2);
        picture2.setPicKeyList(openCardTypesStr);
        bankOpenCardParam.getPictures().add(picture1);
        bankOpenCardParam.getPictures().add(picture2);
        bankOpenCardParam.setCmpseq(serNo);
        bankOpenCardParam.setFileNum(String.valueOf(2));
    }

    /**
     * 废弃
     *
     * @param list
     */
    private void asyncPush(List<String> list) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                list.parallelStream().forEach(e -> {
                    FtpUtil.icbcUpload(e);
                });

            }
        });
    }


}
