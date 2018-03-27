package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class TaskListVO {
    private String id;
    private String customer;
    private String orderGmtCreate;
    private String idCard;
    private String mobile;
    private String salesman;
    private String creditGmtCreate;
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
    private String supplementType;
    private String supplementContent;
    private String supplementStartTime;

    private String currentTask;
}


