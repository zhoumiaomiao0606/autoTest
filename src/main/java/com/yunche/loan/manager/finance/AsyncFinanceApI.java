package com.yunche.loan.manager.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.util.EventBusCenter;
import com.yunche.loan.config.util.GeneratorIDUtil;
import com.yunche.loan.config.util.HttpUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.PostFinanceData;
import com.yunche.loan.mapper.*;
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

import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_PASS;

@Component
public class AsyncFinanceApI {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncFinanceApI.class);

    private static final String HOST = "http://47.96.78.20:8012";

    private static final String PATH = "/costcalculation/insert";

    private static final String METHOD = "post";

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private RemitDetailsDOMapper remitDetailsDOMapper;

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
            postFinanceData.setClientAdvance(remitDetailsDO.getRemit_amount());

            postFinanceData.setType(IDict.K_VOUCHER.K_VOUCHER_1);

            postFinanceData.setCompanyId(remitDetailsDO.getRemit_business_id());


        }

        //退款申请
        if (approvalParam.getTaskDefinitionKey().equals(REFUND_APPLY.getCode()) && ACTION_PASS.equals(approvalParam.getAction())) {
            LoanRefundApplyDO loanRefundApplyDO = loanRefundApplyDOMapper.lastByOrderId(approvalParam.getOrderId());
            Preconditions.checkNotNull(loanRefundApplyDO, "退款单为空");

            postFinanceData.setAmountMoney(String.valueOf(loanRefundApplyDO.getRefund_amount()));//打款金额
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
        if(approvalParam.getTaskDefinitionKey().equals(BANK_LEND_RECORD.getCode()) && ACTION_PASS.equals(approvalParam.getAction())){

        }

        //进行推送
        try {
            LOG.info("准备异步发送数据！！！" + postFinanceData.toString());
            String retJson = HttpUtils.doPost(HOST, PATH, null, postFinanceData.toString());
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
        if ((approvalParam.getTaskDefinitionKey().equals(REMIT_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction())) || (approvalParam.getTaskDefinitionKey().equals(FINANCE_INSTEAD_PAY_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction())) || (approvalParam.getTaskDefinitionKey().equals(REFUND_APPLY.getCode()) && ACTION_PASS.equals(approvalParam.getAction()))) {
            postFinanceData(approvalParam);
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
