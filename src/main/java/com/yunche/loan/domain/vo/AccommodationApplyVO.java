package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class AccommodationApplyVO {


    private String customer;



    private String orderGmtCreate;
    private String idCard;
    private String mobile;
    private String remitGmtCreate;
    private String bankId;
    private String bank;
    private String departmentName;
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
    private String processId;
    private String insteadPayOrderId;
    private String currArrears;
    private String loanBanlance;
    private String advancesBanlance;
    private String overdueDays;
    private String overdueNumber;
    private String advancesNumber;
    private String riskTakingRatio;
    private String applyCompensationDate;
    // 未加
    private String taskStatus;
    private String supplementOrderId;
    private String supplementType;
    private String supplementTypeText;
    private String supplementContent;
    private String supplementStartTime;
    private String applyLicensePlateArea;
    private String printGmtCreate;

    private String car_detail_id;
    // 已加
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
    private String paymentGtCreate;
    private String carName;
    private String carGpsNum;
    private String compensationAmount;
    private String dataFlowId;
    private String dataFlowType;
    private String dataFlowTypeText;
    private String visitDoorId;
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
    /**
     * [金融方案修改申请单] ID
     */
    private Long his_id;
    /**
     * [金融方案修改申请单] 创建时间
     */
    private String his_start_time;
    /**
     * [退款申请单] ID
     */
    private Long refund_id;
    /**
     * [退款申请单] 创建时间
     */
    private String refund_start_time;

    private String lendCard;
}
