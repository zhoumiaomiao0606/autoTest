package com.yunche.loan.domain.vo;

import lombok.Data;

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

    private String remitDate;

    private String overDueDays;
}
