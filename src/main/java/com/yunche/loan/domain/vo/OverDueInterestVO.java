package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class OverDueInterestVO
{

    private String orderId;

    private String customerName;

    private String idCard;

    private String partnerName;

    private String bank;

    private String loanAmount;

    private String remitAmount;

    //银行分期本金
    private String bankPeriodPrincipal;

    //执行利率
    private String signRate;

    private String remitDate;

    private String overDueDays;

    private String vehicleInfoOverDays;

    private String overInterest;

}
