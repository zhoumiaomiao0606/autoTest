package com.yunche.loan.manager.finance;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.yunche.loan.config.common.FinanceConfig;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.feign.client.TenantFeignClient;
import com.yunche.loan.config.util.*;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.DistributorParam;
import com.yunche.loan.domain.param.PostFinanceData;
import com.yunche.loan.domain.vo.DistributorVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanProcessLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static com.yunche.loan.config.constant.LoanOrderProcessConst.ORDER_STATUS_CANCEL;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_PASS;

@Component
public class AsyncFinanceApI {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncFinanceApI.class);

    //private static final String HOST = "http://47.96.78.20:8012";

    private static final String PATH = "/costcalculation/insert";

    @Autowired
    private FinanceConfig financeConfig;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private RemitDetailsDOMapper remitDetailsDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private CostDetailsDOMapper costDetailsDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanRefundApplyDOMapper loanRefundApplyDOMapper;

    @Autowired
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;

    @Autowired
    private BankDOMapper bankDOMapper;

    @Autowired
    private VoucherErrRecordDOMapper voucherErrRecordDOMapper;

    @Autowired
    private VoucherRecordDOMapper voucherRecordDOMapper;

    @Autowired
    private LoanProcessInsteadPayDOMapper loanProcessInsteadPayDOMapper;

    @Autowired
    private LoanProcessLogService loanProcessLogService;

    @Autowired
    private PartnerDOMapper partnerDOMapper;

    @Autowired
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Autowired
    private CarDetailDOMapper carDetailDOMapper;

    @Autowired
    private CarModelDOMapper carModelDOMapper;





    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private TenantFeignClient tenantFeignClient;


    private  RemitDetailsDO remit2FinanceVoucher(Long orderId){
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);

        // 关联ID
        Long remitDetailsId = loanOrderDO.getRemitDetailsId();

        Preconditions.checkNotNull(remitDetailsId, "打款详单为空");

        RemitDetailsDO remitDetailsDO = remitDetailsDOMapper.selectByPrimaryKey(remitDetailsId);
        return remitDetailsDO;
    }
    @Async
    public void postFinanceData(ApprovalParam approvalParam) {

        //进行推送
        try {
        PostFinanceData postFinanceData = new PostFinanceData();

        //根据orderid查询合伙人id
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getByOrderId(approvalParam.getOrderId());
        Preconditions.checkNotNull(loanBaseInfoDO.getPartnerId(), "合伙人id不能为空");
        postFinanceData.setPartnerId(loanBaseInfoDO.getPartnerId());


        Long bankId = bankDOMapper.selectIdByName(loanBaseInfoDO.getBank());
        postFinanceData.setBankId(bankId);
        //判断是-垫款提交-退款提交-偿款提交----查询相关数据

        // 1代客户垫款
        // 2收到银行款项
        // 3客户退款
        // 4公司代客户偿款
        //打款确认
        RemitDetailsDO remitDetailsDO = remit2FinanceVoucher(approvalParam.getOrderId());
        if (approvalParam.getTaskDefinitionKey().equals(REMIT_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction())) {
            postFinanceData.setAmountMoney(String.valueOf(remitDetailsDO.getRemit_amount()));//打款金额

            postFinanceData.setType(IDict.K_VOUCHER.K_VOUCHER_1);

            postFinanceData.setCompanyId(remitDetailsDO.getRemit_business_id());


        }

        //退款申请
        if (approvalParam.getTaskDefinitionKey().equals(REFUND_APPLY_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction())) {
            LoanRefundApplyDO loanRefundApplyDO = loanRefundApplyDOMapper.lastByOrderId(approvalParam.getOrderId());
            Preconditions.checkNotNull(loanRefundApplyDO, "退款单为空");

            postFinanceData.setAmountMoney(String.valueOf(remitDetailsDO.getRemit_amount()));//打款金额
            postFinanceData.setAdvancesInterest(String.valueOf(loanRefundApplyDO.getAdvances_interest()));//垫款利息收入
            postFinanceData.setOtherInterest(String.valueOf(loanRefundApplyDO.getOther_interest()));//其他利息收入
            postFinanceData.setPenaltyInterest(String.valueOf(loanRefundApplyDO.getPenalty_interest()));//罚息收入
            postFinanceData.setType(IDict.K_VOUCHER.K_VOUCHER_3);
            postFinanceData.setCompanyId(remitDetailsDO.getRemit_business_id());

        }


        //财务代偿-确认 008
        if (approvalParam.getTaskDefinitionKey().equals(FINANCE_INSTEAD_PAY_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction())) {
            LoanProcessInsteadPayDO loanProcessInsteadPayDO = loanProcessInsteadPayDOMapper.selectByPrimaryKey(approvalParam.getProcessId());
            Long id = loanProcessInsteadPayDO.getBankRepayImpRecordId();//代偿表中的id
            LoanApplyCompensationDO applyCompensationDO = loanApplyCompensationDOMapper.selectByPrimaryKey(id);
            postFinanceData.setType(IDict.K_VOUCHER.K_VOUCHER_8);
//            postFinanceData.setClientAdvance(totalCompensationAcount);
            BigDecimal compensationAmount = applyCompensationDO.getCompensationAmount();
            BigDecimal riskTakingRatio = applyCompensationDO.getRiskTakingRatio();
            BigDecimal partnerCompensationAmount = compensationAmount.multiply(riskTakingRatio.divide(new BigDecimal("100")));//合伙人代偿金额

            postFinanceData.setCompanySubrogationFund(String.valueOf(compensationAmount.subtract(partnerCompensationAmount)));

            postFinanceData.setPartnerSubrogationFund(String.valueOf(partnerCompensationAmount));

            postFinanceData.setSubrogationFundInterest(String.valueOf(applyCompensationDO.getCompensationInterest()));

            postFinanceData.setSubrogationFundIncome(String.valueOf(compensationAmount));
            postFinanceData.setCompanyId("1");
        }

        //--002 银行放款
        if(approvalParam.getTaskDefinitionKey().equals(BANK_LEND_RECORD.getCode()) && ACTION_PASS.equals(approvalParam.getAction()))
        {
            /*LoanRefundApplyDO loanRefundApplyDO = loanRefundApplyDOMapper.lastByOrderId(approvalParam.getOrderId());
            Preconditions.checkNotNull(loanRefundApplyDO, "退款单为空");
            postFinanceData.setAdvancesInterest(String.valueOf(loanRefundApplyDO.getAdvances_interest()));//垫款利息收入
            postFinanceData.setOtherInterest(String.valueOf(loanRefundApplyDO.getOther_interest()));//其他利息收入
            postFinanceData.setPenaltyInterest(String.valueOf(loanRefundApplyDO.getPenalty_interest()));//罚息收入*/

            postFinanceData.setCompanyId(remitDetailsDO.getRemit_business_id());

            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(approvalParam.getOrderId());
            //查询金融方案
            //查询花费明细
            LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
            CostDetailsDO costDetailsDO = costDetailsDOMapper.selectByPrimaryKey(loanOrderDO.getCostDetailsId());



            if (loanFinancialPlanDO==null)
            {
                throw new BizException("无金融方案信息");
            }
            if (costDetailsDO==null)
            {
                throw new BizException("无花费明细信息");
            }


            postFinanceData.setBankDeposits(loanFinancialPlanDO.getBankPeriodPrincipal());
            postFinanceData.setCarLoanMoney(remitDetailsDO.getRemit_amount());
            postFinanceData.setPartnerRebates(remitDetailsDO.getReturn_rate_amount());
            postFinanceData.setMortgageDeposit(costDetailsDO.getApply_license_plate_deposit_fee());
            postFinanceData.setRiskFee(costDetailsDO.getRisk_fee());
            postFinanceData.setCustomerDeposit(costDetailsDO.getPerformance_fee());
            postFinanceData.setAssessmentIncome(costDetailsDO.getFair_assess_fee());
            postFinanceData.setOtherIncome(costDetailsDO.getOther_fee());
            postFinanceData.setCardIncome(costDetailsDO.getApply_license_plate_out_province_fee());
            postFinanceData.setGpsIncome(costDetailsDO.getInstall_gps_fee());

            //新加
            postFinanceData.setCompanyIncome(costDetailsDO.getService_fee().toString());


            postFinanceData.setType(IDict.K_VOUCHER.K_VOUCHER_2);

        }


            LOG.info("准备异步发送数据！！！" + postFinanceData.toString());
            String retJson = HttpUtils.doPost(financeConfig.getAccountingVoucherHost(), PATH, null, postFinanceData.toString());
            //记录流水信息
            errSerialRecord(retJson,approvalParam);

            LOG.info("应答数据：" + retJson);

        } catch (Exception e)
        {
            //这里也要记录
            if (approvalParam.getSerial_no()==null)
            {
                String execute = GeneratorIDUtil.execute();
                VoucherErrRecordDO voucherErrRecordDO = new VoucherErrRecordDO();
                voucherErrRecordDO.setSerialNo(execute);
                voucherErrRecordDO.setOrderId(approvalParam.getOrderId());
                voucherErrRecordDO.setTaskDefinitionKey(approvalParam.getTaskDefinitionKey());
                voucherErrRecordDO.setCreateTime(new Date());
                voucherErrRecordDO.setProcessId(approvalParam.getProcessId());
                voucherErrRecordDO.setRetMessage("发送异常或者接口不通");
                voucherErrRecordDOMapper.insertSelective(voucherErrRecordDO);


            }
            LOG.error("财务数据异步发送失败！！！", e);
        }


    }

    /**
     * 记录异常流水
     * @param
     */
    private void errSerialRecord(String retJson,ApprovalParam approvalParam){
        ObjectMapper objectMapper = new ObjectMapper();
        if (approvalParam.getSerial_no()==null)
        {
            String execute = GeneratorIDUtil.execute();
            VoucherErrRecordDO voucherErrRecordDO = new VoucherErrRecordDO();
            voucherErrRecordDO.setSerialNo(execute);
            voucherErrRecordDO.setOrderId(approvalParam.getOrderId());
            voucherErrRecordDO.setTaskDefinitionKey(approvalParam.getTaskDefinitionKey());
            voucherErrRecordDO.setCreateTime(new Date());
            voucherErrRecordDO.setProcessId(approvalParam.getProcessId());

            try {
                Map map = objectMapper.readValue(retJson, Map.class);
                voucherErrRecordDO.setRetStatus(String.valueOf(map.get("resultCode")));
                voucherErrRecordDO.setRetMessage(String.valueOf(map.get("message")));
                if(String.valueOf(map.get("resultCode")).equals("200")){
                    voucherErrRecordDO.setStatus(new Byte("2"));

                    //记录会计凭证号
                    VoucherRecordDO voucherRecordDO = new VoucherRecordDO();
                    voucherRecordDO.setOrderId(approvalParam.getOrderId());
                    voucherRecordDO.setOperationNum(execute);
                    voucherRecordDO.setVoucherNum(String.valueOf(map.get("datas")));
                    voucherRecordDO.setGmtCreate(new Date());
                    voucherRecordDOMapper.insertSelective(voucherRecordDO);
                }
            } catch (IOException e)
            {
                voucherErrRecordDO.setRetMessage(retJson);
            }

            voucherErrRecordDOMapper.insertSelective(voucherErrRecordDO);
        }else
            {
                VoucherErrRecordDO voucherErrRecordDO = voucherErrRecordDOMapper.selectByPrimaryKey(approvalParam.getSerial_no());
                try {
                    Map map = objectMapper.readValue(retJson, Map.class);
                    voucherErrRecordDO.setRetStatus(String.valueOf(map.get("resultCode")));
                    voucherErrRecordDO.setRetMessage(String.valueOf(map.get("message")));
                    if(String.valueOf(map.get("resultCode")).equals("200")){
                        voucherErrRecordDO.setStatus(new Byte("2"));

                        //记录会计凭证号
                        VoucherRecordDO voucherRecordDO = new VoucherRecordDO();
                        voucherRecordDO.setOrderId(approvalParam.getOrderId());
                        voucherRecordDO.setOperationNum(approvalParam.getSerial_no());
                        voucherRecordDO.setVoucherNum(String.valueOf(map.get("datas")));
                        voucherRecordDO.setGmtCreate(new Date());
                        voucherRecordDOMapper.insertSelective(voucherRecordDO);
                    }
                } catch (IOException e)
                {
                    voucherErrRecordDO.setRetMessage(retJson);
                }

                voucherErrRecordDOMapper.updateByPrimaryKeySelective(voucherErrRecordDO);

            }

    }
    //一旦-垫款提交-退款提交-偿款提交-则执行
    @Subscribe
    public void listernApproval(ApprovalParam approvalParam) {
        if ((approvalParam.getTaskDefinitionKey().equals(REMIT_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction()))
                || (approvalParam.getTaskDefinitionKey().equals(FINANCE_INSTEAD_PAY_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction()))
                || (approvalParam.getTaskDefinitionKey().equals(REFUND_APPLY_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction()))
                || (approvalParam.getTaskDefinitionKey().equals(BANK_LEND_RECORD.getCode()) && ACTION_PASS.equals(approvalParam.getAction()))) {
            postFinanceData(approvalParam);
        }


        //通知财务系统信息
        if(approvalParam.getTaskDefinitionKey().equals(CREDIT_APPLY.getCode())
                || approvalParam.getTaskDefinitionKey().equals(LOAN_APPLY.getCode())
                || approvalParam.getTaskDefinitionKey().equals(REFUND_APPLY_REVIEW.getCode())
                || approvalParam.getTaskDefinitionKey().equals(REMIT_REVIEW.getCode())
                || approvalParam.getTaskDefinitionKey().equals(BUSINESS_PAY)){

            orderInfoPush(approvalParam);
        }

        //交易后处理
        if(approvalParam.getTaskDefinitionKey().equals(REMIT_REVIEW.getCode())){

            afterProcess(approvalParam);
        }
    }

    /**
     * 交易后处理
     * @param approvalParam
     */
    private void afterProcess(ApprovalParam approvalParam) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(approvalParam.getOrderId());
        if(approvalParam.getTaskDefinitionKey().equals(REMIT_REVIEW.getCode().trim()) && approvalParam.getAction().equals(ACTION_PASS)){
            RemitDetailsDO remitDetailsDO = remitDetailsDOMapper.selectByPrimaryKey(loanOrderDO.getRemitDetailsId());
            remitDetailsDO.setRemit_status(IDict.K_DKZT.PAY_SUCC);
            remitDetailsDOMapper.updateByPrimaryKeySelective(remitDetailsDO);
        }
    }

    /**
     * 征信申请时 、 贷款申请时 通知财务系统订单信息
     * @param approvalParam
     */
    @Async
    private void orderInfoPush(ApprovalParam approvalParam) {


        DistributorParam distributorParam = new DistributorParam();
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(approvalParam.getOrderId());
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), null);
        LoanProcessLogDO loanProcessLog = loanProcessLogService.getLoanProcessLog(approvalParam.getOrderId(), CREDIT_APPLY.getCode());
        LoanProcessLogDO remitloanProcessLog = loanProcessLogService.getLoanProcessLog(approvalParam.getOrderId(), REMIT_REVIEW.getCode());
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(loanBaseInfoDO.getPartnerId(), null);
        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());

        distributorParam.setOrderId(String.valueOf(approvalParam.getOrderId()));
        distributorParam.setName(loanCustomerDO.getName());
        distributorParam.setIdCard(loanCustomerDO.getIdCard());

        distributorParam.setCreditOperatorName(loanProcessLog==null?null:loanProcessLog.getUserName());
        distributorParam.setCreateDate(DateUtil.getDateTo10(loanProcessLog.getCreateTime()));
        distributorParam.setPartnerId(loanBaseInfoDO.getPartnerId().toString());
        distributorParam.setPartnerName(partnerDO.getName());
        distributorParam.setLoanTime(DateUtil.getDateTo10(remitloanProcessLog.getCreateTime()));


        if(loanOrderDO.getLoanFinancialPlanId()!=null){
            LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
            if(loanFinancialPlanDO!=null){
                distributorParam.setCarPrice(BigDecimalUtil.format(loanFinancialPlanDO.getCarPrice(),2));
            }
        }

        if(loanCarInfoDO!=null){
            CarDetailDO carDetailDO = carDetailDOMapper.selectByPrimaryKey(loanCarInfoDO.getCarDetailId(), null);
            CarModelDO carModelDO = carModelDOMapper.selectByPrimaryKey(carDetailDO.getModelId(), null);

            distributorParam.setCarDetail(String.valueOf(loanCarInfoDO.getCarDetailId()));
            distributorParam.setCarModel(String.valueOf(carDetailDO.getModelId()));
            distributorParam.setCarBrand(String.valueOf(carModelDO.getBrandId()));
            distributorParam.setTenantId(loanCarInfoDO.getDistributorId());
        }

        RemitDetailsDO remitDetailsDO = remitDetailsDOMapper.selectByPrimaryKey(loanOrderDO.getRemitDetailsId());
        if(remitDetailsDO!=null){
            distributorParam.setTenantRebate(remitDetailsDO.getCar_dealer_rebate().toString());

        }
        //进行推送
        try {
            DistributorVO distributorVO =null;
                    /**
                     * 订单生成 调用 新增方法   orderStatus = 1

                       财务垫款      修改方法   orderStatus= 2

                       弃单/退款 调用 修改方法   orderStatus=3
                     */

            LOG.info("准备异步发送数据！！！" + JSONObject.toJSON(distributorParam).toString());



            if(approvalParam.getTaskDefinitionKey().equals(CREDIT_APPLY.getCode())){

                distributorParam.setOrderStatus("1");
                //弃单
                if(approvalParam.getAction().equals(ORDER_STATUS_CANCEL)){

                    distributorParam.setOrderStatus("3");
                }
                distributorVO = tenantFeignClient.saveOrder(distributorParam);
            }else {
                //打款确认
                if(approvalParam.getTaskDefinitionKey().equals(REMIT_REVIEW.getCode())){
                    distributorParam.setOrderStatus("2");

                }else if(approvalParam.getTaskDefinitionKey().equals(REFUND_APPLY_REVIEW.getCode())){
                    distributorParam.setOrderStatus("3");
                }
                //弃单
                if(approvalParam.getAction().equals(ORDER_STATUS_CANCEL)){
                    distributorParam.setOrderStatus("3");
                }
                distributorVO = tenantFeignClient.modifyOrder(distributorParam.getOrderId(), distributorParam);
            }

            LOG.info("应答数据：" + JSONObject.toJSON(distributorVO).toString());

        } catch (Exception e) {
            LOG.error(approvalParam.getOrderId()+" "+approvalParam.getTaskDefinitionKey()+":财务数据异步发送失败！！！", e);
        }

    }


    // 由spring 在初始化bean后执行
    @PostConstruct
    public void init() {
        register2EventBus();
    }

    // 将自己注册到eventBus中
    protected void register2EventBus() {
        EventBusCenter.eventBus.register(this);
    }
}
