package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class ContractOverDueParam
{
    private String customerName;

    private String loanBank;

    private String orderId;

    private Long partnerId;

    private String remitTimeStart;

    private String remitTimeEnd;


    private Integer pageIndex = 1;

    private Integer pageSize = 20;
}
