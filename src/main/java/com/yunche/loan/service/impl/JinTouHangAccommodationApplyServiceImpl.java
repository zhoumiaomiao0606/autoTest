package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.constant.ProcessApprovalConst;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.*;
import com.yunche.loan.config.util.Process;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.AccommodationApplyParam;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.ExportApplyLoanPushParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.JinTouHangAccommodationApplyService;
import com.yunche.loan.service.LoanProcessBridgeService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.LoanProcessEnum.BRIDGE_HANDLE;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_ROLL_BACK;

@Service
@Transactional

public class JinTouHangAccommodationApplyServiceImpl implements JinTouHangAccommodationApplyService {

    private Logger logger = LoggerFactory.getLogger(JinTouHangAccommodationApplyServiceImpl.class);
    @Autowired
    private ThirdPartyFundBusinessDOMapper thirdPartyFundBusinessDOMapper;

    @Autowired
    private LoanProcessBridgeService loanProcessBridgeService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private LoanStatementDOMapper loanStatementDOMapper;


    @Autowired
    private ConfThirdRealBridgeProcessDOMapper confThirdRealBridgeProcessDOMapper;

    @Autowired
    private ConfThirdPartyMoneyDOMapper confThirdPartyMoneyDOMapper;

    @Autowired
    private BankCache bankCache;

    @Autowired
    private BankLendRecordDOMapper bankLendRecordDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanHomeVisitDOMapper loanHomeVisitDOMapper;

    @Resource
    private AsyncUpload asyncUpload;


//    @Override
//    public ResultBean revoke(AccommodationApplyParam param) {
//        return null;
//    }

    /**
     * 提交接口 业务后处理
     *
     * @param param
     * @return
     */
    @Override
    public void dealTask(ApprovalParam param) {

        //金投行过桥处理 -反审
        if (ACTION_ROLL_BACK.equals(param.getAction()) && BRIDGE_HANDLE.getCode().equals(param.getTaskDefinitionKey())) {
            ThirdPartyFundBusinessDO businessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(param.getProcessId());
            businessDO.setLendStatus(IDict.K_CJZT.K_CJZT_NO);
            int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(businessDO);
            Preconditions.checkArgument(count > 0, "更新状态[未出借]失败");
        }

    }

    /**
     * 拒绝出借
     *
     * @param param
     * @return
     */
    @Override
    public ResultBean reject(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair(), "参数有误");
        Preconditions.checkNotNull(param.getLendStatus(), "出借状态不能为空");
        int count;
        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(param.getIdPair().getBridgeProcessId());
        ThirdPartyFundBusinessDO fundBusinessDO = new ThirdPartyFundBusinessDO();
        fundBusinessDO.setOrderId(param.getIdPair().getOrderId());
        fundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
        fundBusinessDO.setLendStatus(param.getLendStatus());
        if (thirdPartyFundBusinessDO == null) {
            fundBusinessDO.setGmtCreate(new Date());
            count = thirdPartyFundBusinessDOMapper.insertSelective(fundBusinessDO);
        } else {
            count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(fundBusinessDO);
        }
        Preconditions.checkArgument(count > 0, "拒绝出借操作失败");
        return ResultBean.ofSuccess(null, "拒绝出借操作成功");
    }

    /**
     * 借款
     *
     * @return
     */
    @Override
    public ResultBean applyLoan(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair(), "参数有误");

        ThirdPartyFundBusinessDO aDo = new ThirdPartyFundBusinessDO();
        BeanUtils.copyProperties(param, aDo);
        aDo.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
        aDo.setOrderId(param.getIdPair().getOrderId());
        aDo.setLendStatus(IDict.K_CJZT.K_CJZT_INHAND);
        aDo.setLendAmount(param.getLendAmount());
        ThirdPartyFundBusinessDO fundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(param.getIdPair().getBridgeProcessId());
        int count;
        if (fundBusinessDO != null) {
            count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(aDo);
        } else {
            count = thirdPartyFundBusinessDOMapper.insertSelective(aDo);
        }
        Preconditions.checkArgument(count > 0, "保存失败");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(param.getIdPair().getOrderId());
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(),new Byte("0"));
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        LoanHomeVisitDO loanHomeVisitDO = loanHomeVisitDOMapper.selectByPrimaryKey(loanOrderDO.getLoanHomeVisitId());
        asyncUpload.execute(new Process() {
            @Override
            public void process() {
                JTXCommunicationUtil jtxCommunicationUtil = new JTXCommunicationUtil();
                Boolean flag = jtxCommunicationUtil.borrowerInfoAuth(loanCustomerDO.getName(),loanCustomerDO.getIdCard(),loanCustomerDO.getMobile(),
                        loanBaseInfoDO.getBank(),loanHomeVisitDO.getDebitCard(),param.getIdPair());
                if(flag){
                    //jtxCommunicationUtil.assetRelease();
                }else{
                    ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();

                }
            }
        });


        return ResultBean.ofSuccess("借款申请成功");
    }

    /**
     * 批量贷款
     *
     * @return
     */
    @Override
    public ResultBean batchLoan(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        List<AccommodationApplyParam.IDPair> idPairs = param.getIdPairs();
//        List<Long> orderIds = param.getOrderIds();

        List<ThirdPartyFundBusinessDO> collect = idPairs.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    ThirdPartyFundBusinessDO aDo = new ThirdPartyFundBusinessDO();
                    aDo.setBridgeProcecssId(e.getBridgeProcessId());
                    aDo.setOrderId(e.getOrderId());
                    aDo.setLendDate(param.getLendDate());
                    aDo.setGmtCreate(new Date());

                    ThirdPartyFundBusinessDO fundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(e.getBridgeProcessId());
                    int count;
                    if (fundBusinessDO != null) {
                        count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(aDo);
                    } else {
                        count = thirdPartyFundBusinessDOMapper.insertSelective(aDo);
                    }
                    Preconditions.checkArgument(count > 0, "插入失败");
                    return aDo;
                }).collect(Collectors.toList());

        //批量导入、提交
        if (!CollectionUtils.isEmpty(collect)) {

            ApprovalParam approvalParam = new ApprovalParam();
            approvalParam.setTaskDefinitionKey(BRIDGE_HANDLE.getCode());
            approvalParam.setAction(ProcessApprovalConst.ACTION_PASS);
            approvalParam.setNeedLog(true);
            approvalParam.setCheckPermission(false);

            //提交任务
            collect.stream().forEach(e -> {

                approvalParam.setOrderId(e.getOrderId());
                approvalParam.setProcessId(e.getBridgeProcecssId());


                ResultBean<Void> approvalResultBean = loanProcessBridgeService.approval(approvalParam);
                Preconditions.checkArgument(approvalResultBean.getSuccess(), approvalResultBean.getMsg());
            });
        }
        return ResultBean.ofSuccess("借款申请成功");
    }

    /**
     * 导入文件
     *
     * @param key
     * @return
     */
    @Override
    public ResultBean batchImp(String key) {
        Preconditions.checkNotNull(key, "文件key不能为空");
        List<ThirdPartyFundBusinessDO> partyFundBusinessDOList = Lists.newArrayList();

        try {
            List<String[]> rowList = POIUtil.readExcelFromOSS(0, 1, key);
            if (!CollectionUtils.isEmpty(rowList)) {
                Preconditions.checkArgument(rowList.size() <= 2000, "最大支持导入2000条数据，当前条数：" + rowList.size());

                for (int i = 0; i < rowList.size(); i++) {
                    // 当前行数
                    int rowNum = i + 1;

                    String[] row = rowList.get(i);
                    // 空行跳过
                    if (ArrayUtils.isEmpty(row)) {
                        continue;
                    }
                    ThirdPartyFundBusinessDO partyFundBusinessDO = new ThirdPartyFundBusinessDO();
                    try {
                        partyFundBusinessDO.setBridgeProcecssId(Long.valueOf(row[0]));//流水号
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第1列格式有误：" + row[0]);
                    }


                    if (StringUtil.isEmpty(row[7].trim())) {
                        throw new BizException("第" + rowNum + "行，第8列格式有误：【借款金额不能为空】");
                    }
                    try {
                        partyFundBusinessDO.setLendAmount(new BigDecimal(row[7]));//乙方借款金额（导入）
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第8列格式有误：" + row[7]);
                    }
                    try {
                        partyFundBusinessDO.setOrderId(Long.valueOf(row[10]));//借据号
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第11列格式有误：" + row[10]);
                    }
                    partyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_YES);

                    //添加数据
                    partyFundBusinessDOList.add(partyFundBusinessDO);

                }

                ApprovalParam approvalParam = new ApprovalParam();
                approvalParam.setTaskDefinitionKey(BRIDGE_HANDLE.getCode());
                approvalParam.setAction(ProcessApprovalConst.ACTION_PASS);
                approvalParam.setNeedLog(true);
                approvalParam.setCheckPermission(false);
                //插入数据库
                partyFundBusinessDOList.stream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {

                            //提交任务
                            approvalParam.setOrderId(e.getOrderId());
                            approvalParam.setProcessId(e.getBridgeProcecssId());

                            ResultBean<Void> approvalResultBean = loanProcessBridgeService.approval(approvalParam);
                            Preconditions.checkArgument(approvalResultBean.getSuccess(), approvalResultBean.getMsg());

                            //更新状态为已出借
                            ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(e.getBridgeProcecssId());
                            int count;
                            if (thirdPartyFundBusinessDO == null) {
                                count = thirdPartyFundBusinessDOMapper.insertSelective(e);
                            } else {
                                count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(e);
                            }
                            Preconditions.checkArgument(count > 0, "更新状态[已出借]失败");
                        });
            }

        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }

        return ResultBean.ofSuccess(null,"导入成功!");
    }

    /**
     * 金投行过桥处理 -导出
     *
     * @return
     */
    @Override
    public ResultBean export(ExportApplyLoanPushParam param) {

        List<ExportApplyLoanPushVO> voList = loanStatementDOMapper.exportApplyLoanPush(param);
        List<String> header = Lists.newArrayList("流水号", "委托人（购车人、借款人）", "身份证号",
                "车辆品牌型号", "车价", "首付款", "甲方垫款金额（导出）", "乙方借款金额（导入）", "借款期限", "利率", "借据号", "最终放款银行"
        );
        //生成Excel文件
        String ossResultKey = POIUtil.createExcelFile("购车融资业务推送清单", voList, header, ExportApplyLoanPushVO.class, ossConfig);

        // 更新记录为已导出
        voList.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {
                    ThirdPartyFundBusinessDO fundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(Long.valueOf(e.getBridgeProcessId()));
                    if (fundBusinessDO == null) {
                        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
                        thirdPartyFundBusinessDO.setBridgeProcecssId(Long.valueOf(e.getBridgeProcessId()));
                        thirdPartyFundBusinessDO.setOrderId(Long.valueOf(e.getLoanForm()));
                        thirdPartyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_NO);
                        thirdPartyFundBusinessDO.setGmtCreate(new Date());
                        thirdPartyFundBusinessDOMapper.insertSelective(thirdPartyFundBusinessDO);
                    } else {
                        fundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_NO);
                        int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(fundBusinessDO);
                        Preconditions.checkArgument(count > 0, "更新失败");
                    }
                });

        return ResultBean.ofSuccess(ossResultKey);
    }

    /**
     * 金投行还款信息 -导出
     *
     * @return
     */
    @Override
    public ResultBean exportJinTouHangRepayInfo(ExportApplyLoanPushParam param) {

        List<JinTouHangRepayInfoVO> voList = loanStatementDOMapper.exportJinTouHangRepayInfo(param);
        List<String> header = Lists.newArrayList("主贷姓名", "身份证号", "借款时间",
                "银行放款时间", "还款时间", "借款天数", "借款金额", "放款本金", "放款-借款", "还款类型", "利息", "手续费", "备注"
        );

        String ossResultKey = POIUtil.createExcelFile("金投行还款信息", voList, header, JinTouHangRepayInfoVO.class, ossConfig);
        return ResultBean.ofSuccess(ossResultKey);
    }

    /**
     * 金投行息费登记 -导出
     *
     * @return
     */
    @Override
    public ResultBean exportJinTouHangInterestRegister(ExportApplyLoanPushParam param) {

        List<JinTouHangInterestRegisterVO> voList = loanStatementDOMapper.exportJinTouHangInterestRegister(param);
        List<String> header = Lists.newArrayList("借款时间", "还款时间", "借款金额",
                "主贷姓名", "身份证号", "分期本金"
        );

        String ossResultKey = POIUtil.createExcelFile("金投行息费登记", voList, header, JinTouHangInterestRegisterVO.class, ossConfig);
        return ResultBean.ofSuccess(ossResultKey);
    }

    @Override
    public ResultBean calMoney(Long bridgeProcessId, Long orderId, String repayDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        CalMoneyVO calMoneyVO = new CalMoneyVO();
        Long conf_third_party_id;
        BigDecimal yearRate;
        BigDecimal singleRate;
        BigDecimal lend_amount;
        Date lendDate;
        Date repayDate1;
        int timeNum;
        try {
            ConfThirdRealBridgeProcessDO confThirdRealBridgeProcessDO = confThirdRealBridgeProcessDOMapper.selectByPrimaryKey(bridgeProcessId);
            if (confThirdRealBridgeProcessDO != null) {
                conf_third_party_id = confThirdRealBridgeProcessDO.getConfThirdPartyId();
                ConfThirdPartyMoneyDO confThirdPartyMoneyDO = confThirdPartyMoneyDOMapper.selectByPrimaryKey(conf_third_party_id);
                yearRate = confThirdPartyMoneyDO.getYearRate();
                singleRate = confThirdPartyMoneyDO.getSingleRate();
                ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);
                lendDate = thirdPartyFundBusinessDO.getLendDate();
                repayDate1 = sdf.parse(repayDate);
                timeNum = (int) ((repayDate1.getTime() - lendDate.getTime()) / (1000 * 3600 * 24));
                lend_amount = thirdPartyFundBusinessDO.getLendAmount();
                if (lend_amount == null) {
                    lend_amount = new BigDecimal("0.00");
                }
                calMoneyVO.setInterest(String.valueOf(yearRate.divide(BigDecimal.valueOf(100)).multiply(lend_amount).multiply(BigDecimal.valueOf(timeNum)).divide(BigDecimal.valueOf(365), 2, BigDecimal.ROUND_HALF_UP)));
                calMoneyVO.setPoundage(String.valueOf(singleRate.divide(BigDecimal.valueOf(100)).multiply(lend_amount).multiply(BigDecimal.valueOf(timeNum)).divide(BigDecimal.valueOf(365), 2, BigDecimal.ROUND_HALF_UP)));
                calMoneyVO.setTimeNum(String.valueOf(timeNum));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBean.ofSuccess(calMoneyVO);
    }

    @Override
    public ResultBean calMoneyDetail(Long bridgeProcessId, Long orderId, String repayDate, String flag) {
        CalMoneyVO calMoneyVO = new CalMoneyVO();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal yearRate;
        BigDecimal singleRate;
        BigDecimal lend_amount;
        Date lendDate;
        Date repayDate1;
        int timeNum;

        Long conf_third_party_id;
        try {
            //0没有还款记录
            if ("0".equals(flag)) {
                ConfThirdRealBridgeProcessDO confThirdRealBridgeProcessDO = confThirdRealBridgeProcessDOMapper.selectByPrimaryKey(bridgeProcessId);
                if (confThirdRealBridgeProcessDO != null) {
                    conf_third_party_id = confThirdRealBridgeProcessDO.getConfThirdPartyId();
                    ConfThirdPartyMoneyDO confThirdPartyMoneyDO = confThirdPartyMoneyDOMapper.selectByPrimaryKey(conf_third_party_id);
                    yearRate = confThirdPartyMoneyDO.getYearRate();
                    singleRate = confThirdPartyMoneyDO.getSingleRate();
                    ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);
                    lendDate = thirdPartyFundBusinessDO.getLendDate();
                    repayDate1 = sdf.parse(repayDate);
                    timeNum = (int) ((repayDate1.getTime() - lendDate.getTime()) / (1000 * 3600 * 24));
                    lend_amount = thirdPartyFundBusinessDO.getLendAmount();
                    if (lend_amount == null) {
                        lend_amount = new BigDecimal("0.00");
                    }
                    calMoneyVO.setInterest(String.valueOf(yearRate.divide(BigDecimal.valueOf(100)).multiply(lend_amount).multiply(BigDecimal.valueOf(timeNum)).divide(BigDecimal.valueOf(365), 2, BigDecimal.ROUND_HALF_UP)));
                    calMoneyVO.setPoundage(String.valueOf(singleRate.divide(BigDecimal.valueOf(100)).multiply(lend_amount).multiply(BigDecimal.valueOf(timeNum)).divide(BigDecimal.valueOf(365), 2, BigDecimal.ROUND_HALF_UP)));
                    calMoneyVO.setTimeNum(String.valueOf(timeNum));
                    calMoneyVO.setSingleRate(String.valueOf(singleRate));
                }
            } else {
                ConfThirdRealBridgeProcessDO confThirdRealBridgeProcessDO = confThirdRealBridgeProcessDOMapper.selectByPrimaryKey(bridgeProcessId);
                if (confThirdRealBridgeProcessDO != null) {
                    conf_third_party_id = confThirdRealBridgeProcessDO.getConfThirdPartyId();
                    ConfThirdPartyMoneyDO confThirdPartyMoneyDO = confThirdPartyMoneyDOMapper.selectByPrimaryKey(conf_third_party_id);
                    singleRate = confThirdPartyMoneyDO.getSingleRate();
                    ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);
                    lendDate = thirdPartyFundBusinessDO.getLendDate();
                    repayDate1 = thirdPartyFundBusinessDO.getRepayDate();
                    timeNum = (int) ((repayDate1.getTime() - lendDate.getTime()) / (1000 * 3600 * 24));
                    calMoneyVO.setInterest(String.valueOf(thirdPartyFundBusinessDO.getInterest()));
                    calMoneyVO.setPoundage(String.valueOf(thirdPartyFundBusinessDO.getPoundage()));
                    calMoneyVO.setTimeNum(String.valueOf(timeNum));
                    calMoneyVO.setSingleRate(String.valueOf(singleRate));
                    calMoneyVO.setReturnDate(sdf.format(repayDate1));
                    calMoneyVO.setReturnType(String.valueOf(thirdPartyFundBusinessDO.getRepayType()));
                }
            }
            BankLendRecordDO bankLendRecordDO = bankLendRecordDOMapper.selectByLoanOrder(orderId);
            if (bankLendRecordDO != null) {
                if (bankLendRecordDO.getLendDate() != null) {

                    Calendar c = Calendar.getInstance();
                    c.setTime(bankLendRecordDO.getLendDate());
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    calMoneyVO.setBankDate(sdf.format(c.getTime()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBean.ofSuccess(calMoneyVO);
    }

    @Override
    public ResultBean isReturn(Long bridgeProcessId, Long orderId) {
        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);
        if (thirdPartyFundBusinessDO != null) {
            if (thirdPartyFundBusinessDO.getRepayDate() == null) {
                return ResultBean.ofSuccess("0");
            } else {
                return ResultBean.ofSuccess(thirdPartyFundBusinessDO.getRepayDate());
            }
        } else {
            return ResultBean.ofSuccess("0");
        }
    }

    @Override
    public byte[] jtxResult(String param) {
        try {
            String xml = JTXByteUtil.decrypt(param,"netwxactive","GBK","des");
            logger.info("ASSET_03返回信息:"+xml);
            Map map = MapXmlUtil.Xml2Map(xml);
        } catch (Exception e) {
            logger.error("解析金投行数据出错",e);
        }
        JTXCommunicationUtil jtxCommunicationUtil = new JTXCommunicationUtil();
        return jtxCommunicationUtil.buildResultInfo("0000","12","12");
    }

    @Override
    public ResultBean detail(Long bridgeProcessId, Long orderId) {
        Preconditions.checkNotNull(bridgeProcessId, "流程ID不能为空");
        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);

        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(orderDO, "订单信息不存在");
        RecombinationVO<Object> recombinationVO = new RecombinationVO<>();

//        ConfThirdRealBridgeProcessDO thirdRealBridgeProcessDO = confThirdRealBridgeProcessDOMapper.selectByPrimaryKey(bridgeProcessId);
//        if(thirdRealBridgeProcessDO!=null){
//            Long confThirdPartyId = thirdRealBridgeProcessDO.getConfThirdPartyId();
//            ConfThirdPartyMoneyDO confThirdPartyMoneyDO = confThirdPartyMoneyDOMapper.selectByPrimaryKey(confThirdPartyId);
//            Long bankId = confThirdPartyMoneyDO.getBankId();
//            String bankName = bankCache.getNameById(bankId);
//            thirdPartyFundBusinessDO.setConfThirdPartyBankName(bankName);
//        }


        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        UniversalCarInfoVO carInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);

        recombinationVO.setInfo(universalInfoVO);
        recombinationVO.setCar(carInfoVO);
        recombinationVO.setFinancial(financialSchemeVO);
        recombinationVO.setCustomers(customers);
        recombinationVO.setPartyFundBusinessVO(thirdPartyFundBusinessDO);

        return ResultBean.ofSuccess(recombinationVO);
    }

    /**
     * 异常还款
     *
     * @param param
     * @return
     */
    @Override
    public ResultBean abnormalRepay(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair().getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(param.getIdPair().getBridgeProcessId(), "流程ID不能为空");

        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();

        BeanUtils.copyProperties(param, thirdPartyFundBusinessDO);
        thirdPartyFundBusinessDO.setOrderId(param.getIdPair().getOrderId());
        thirdPartyFundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());

        int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
        Preconditions.checkArgument(count > 0, "异常还款跟新失败");

        return ResultBean.ofSuccess("保存成功");
    }

    /**
     * 金投行息费登记
     *
     * @param param
     * @return
     */
    @Override
    public ResultBean repayInterestRegister(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair().getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(param.getIdPair().getBridgeProcessId(), "流程ID不能为空");


        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
        BeanUtils.copyProperties(param, thirdPartyFundBusinessDO);
        thirdPartyFundBusinessDO.setOrderId(param.getIdPair().getOrderId());
        thirdPartyFundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
        int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
        Preconditions.checkArgument(count > 0, "金投行还款登记失败");

        return ResultBean.ofSuccess("保存成功");
    }
}
