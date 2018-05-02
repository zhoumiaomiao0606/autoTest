package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class TaskListVO {
    private String id;
    private String salesmanId;
    private String partnerId;
    private String customer;
    private String orderGmtCreate;
    private String idCard;
    private String mobile;
    private String salesman;
    private String creditGmtCreate;
    private String remitGmtCreate;
    private String bank;
    private String departmentName;
    private String loanAmount;
    private String bankPeriodPrincipal;
    private String signRate;
    private String loanGmtCreate;
    private String carType;
    private String licensePlateNumber;
    private String partner;
    private String loanTime;
    private String downPaymentMoney;
    private String overdueNum;
    private String taskStatus;
    private String supplementOrderId;
    private String supplementType;
    private String supplementTypeText;
    private String supplementContent;
    private String supplementStartTime;
    private String lendDate;

    private String currentTask;
    /**
     * 任务类型：1-已提交;  2-未提交;  3-打回;
     */
    private String taskType;
    /**
     * 任务类型文本：1-已提交;  2-未提交;  3-打回;
     */
    private String taskTypeText;

    /**
     * 打回原因
     */
    private String rejectReason;
    /**
     * 订单总状态：1-进行中;2-结单;3-已弃单;
     */
    private String orderStatus;

    /**
     * 是否可以编辑金融方案   -> 当 rejectOriginTask = "usertask_material_review"时，为false(不能编辑)
     */
    private Boolean canUpdateLoanApply = true;
    private String his_start_time;
    private String refund_start_time;
}


