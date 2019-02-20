package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class TaskListVO {

    private String id;
    private String processId;
    private String taskId;
    private String taskKey;
    private String cancelTaskKey;

    private String overdueAmount;
    private String taskDisStatus;
    private String receiveManId;
    private String receiveManName;
    private String remitAmount;
    private String remitTime;
    private String telephoneGmtCreate;
    private String partnerCompensationAmount;
    private String visitGmtCreate;

    private String bankOpenCardTime;

    private String salesmanId;
    private String salesman;
    private String partnerId;
    private String partner;
    private String partnerCode;
    private String partnerGroup;
    private String customerId;
    private String customer;
    private String orderGmtCreate;
    private String idCard;
    private String mobile;
    private String departmentId;
    private String departmentName;
    private String remitGmtCreate;
    private String bankId;
    private String bank;
    private String loanAmount;
    private String bankPeriodPrincipal;
    private String signRate;
    private String loanGmtCreate;
    private String carType;
    private String licensePlateNumber;
    private String loanTime;
    private String downPaymentMoney;
    private String overdueNum;
    private String telephoneVerifyStatus;
    private String bankRepayImpRecordId;
    private String insteadPayOrderId;
    private String currArrears;
    private String loanBanlance;
    private String advancesBanlance;
    private String overdueDays;
    private String overdueNumber;
    private String advancesNumber;
    private String riskTakingRatio;
    private String applyCompensationDate;

    private String taskStatus;
    private String supplementOrderId;
    private String supplementType;
    private String supplementTypeText;
    private String supplementContent;
    private String supplementStartTime;
    private String applyLicensePlateArea;
    private String printGmtCreate;

    private String car_detail_id;
    private String lendDate;
    private String financial_product_name;
    private String car_price;
    private String each_month_repay;
    private String repayDate;
    private String isStraighten;
    private String collectionDate;
    private String isRepayment;
    private String sendeeDate;
    private String sendee;
    private String sendeeName;
    private String urgeGmtCreate;
    private String currentTask;
    private String creditMan;
    private String creditDate;
    private String bankCreditDate;
    private String socialCreditDate;
    private String paymentGtCreate;
    private String carName;
    private String carGpsNum;
    private String compensationAmount;
    private String dataFlowId;
    private String dataFlowType;
    private String dataFlowTypeText;
    private String visitDoorId;

    // 审核人
    private String approvalUserId;
    private String approvalUserName;
    // 审核时间
    private String approvalGmtCreate;

    //////////////////////////[金融方案修改申请单]/////////////////////////
    /**
     * [金融方案修改申请单] ID
     */
    private Long his_id;
    /**
     * [金融方案修改申请单] 创建时间
     */
    private String his_start_time;
    //////////////////////////[金融方案修改申请单]/////////////////////////

    //////////////////////////[退款申请单]////////////////////////////////
    /**
     * [退款申请单] ID
     */
    private Long refund_id;
    /**
     * [退款申请单] 创建时间
     */
    private String refund_start_time;
    /**
     * [退款申请单] 通过时间
     */
    private String refund_end_time;
    /**
     * 退款金额
     */
    private String refund_amount;
    //////////////////////////[退款申请单]////////////////////////////////


    // 未加
    /**
     * 任务类型：1-已提交;  2-未提交;  3-打回;
     */
    private String taskType;
    /**
     * 任务类型文本：1-已提交;  2-未提交;  3-打回;
     */
    private String taskTypeText;
    /*
     * 订单总状态：1-进行中;2-结单;3-已弃单;
     */
    private String orderStatus;

    private String lendCard;

    /////////////////////////////--过桥资金--//////////////////////////////
    private String bridgeLendDate;
    private String bridgeRepayType;
    private String bridgeRepayDate;
    private String bridgeRepayRemark;
    private String bridgeInterest;//利息
    private String bridgePoundage;//手续费
    private String bridgeRepayInterestDate;//还利日期
    private String bridgeRepayRegisterRemark;//还款登记备注
    private String confThirdLoanTime;//借款期限
    private String bridgeLendStatus;//出借状态
    private String bridgeLendAmount;//金投行借款金额

    /////////////////////////////--过桥资金--//////////////////////////////


    private String faceNum;


    // 角色变更
    private String roleChangeHisId;
    private String roleChangeHisCreateTime;
    private String roleChangeHisUserId;
    private String roleChangeHisUserName;


    private String orderRisk;

    private String confThirdLoanMoney;

    //商业险
    private String cIStartTime;

    //交强险
    private String cTALIStartTime;
}


