package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessDO implements LoanProcessDO_ {

    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 征信申请
     */
    private Byte creditApply;
    /**
     * 贷款信息登记
     */
    private Byte loanInfoRecord;
    /**
     * 银行征信
     */
    private Byte bankCreditRecord;
    /**
     * 社会征信
     */
    private Byte socialCreditRecord;
    /**
     * 贷款申请
     */
    private Byte loanApply;
    /**
     * 上门调查
     */
    private Byte visitVerify;
    /**
     * 银行开卡
     */
    private Byte bankOpenCard;
    /**
     * 电审
     */
    private Byte telephoneVerify;
    /**
     * 金融方案
     */
    private Byte financialScheme;
    /**
     * 银行卡接收
     */
    private Byte bankCardRecord;
    /**
     * 车辆保险
     */
    private Byte carInsurance;
    /**
     * 提车资料
     */
    private Byte vehicleInformation;
    /**
     * 车辆抵押
     */
    private Byte applyLicensePlateDepositInfo;
    /**
     * GPS安装
     */
    private Byte installGps;
    /**
     * 待收钥匙
     */
    private Byte commitKey;
    /**
     * 资料审核
     */
    private Byte materialReview;
    /**
     * 合同打印
     */
    private Byte materialPrintReview;
    /**
     * 合同归档
     */
    private Byte materialManage;
    /**
     * 申请分期
     */
    private Byte applyInstalment;
    /**
     * 业务审批
     */
    private Byte businessReview;
    /**
     * 放款审批
     */
    private Byte loanReview;
    /**
     * 打款确认
     */
    private Byte remitReview;
    /**
     * 银行放款记录
     */
    private Byte bankLendRecord;
    /**
     * 银行卡寄送
     */
    private Byte bankCardSend;
    /**
     * 客户还款计划
     */
    private Byte customerRepayPlan;
    /**
     * 业务付款申请
     */
    private Byte businessPay;

    private Date gmtCreate;

    private Date gmtModify;

    /**
     * 弃单任务节点KEY
     */
    private String cancelTaskDefKey;
    /**
     * 当前订单状态(1:进行中;2:已完结;3:已弃单;)
     */
    private Byte orderStatus;


    //////////////////// 资料流转 /////////////////////

    private Byte dataFlowContractP2c;

    private Byte dataFlowContractP2cReview;

    private Byte dataFlowContractC2b;

    private Byte dataFlowContractC2bReview;

    private Byte dataFlowMortgageB2c;

    private Byte dataFlowMortgageB2cReview;

    private Byte dataFlowMortgageC2p;

    private Byte dataFlowMortgageC2pReview;

    private Byte dataFlowMortgageP2c;

    private Byte dataFlowMortgageP2cReview;

    private Byte dataFlowMortgageC2b;

    private Byte dataFlowMortgageC2bReview;

    private Byte dataFlowRegisterP2c;

    private Byte dataFlowRegisterP2cReview;

    private Byte dataFlowRegisterC2b;

    private Byte dataFlowRegisterC2bReview;

    //////////////////// 资料流转 /////////////////////
}