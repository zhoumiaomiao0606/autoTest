package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ContractOverDueVO
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

    private Date remitDate;

    private String overDueDays;
}
