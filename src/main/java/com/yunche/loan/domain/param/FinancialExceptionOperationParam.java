package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class FinancialExceptionOperationParam
{
    private String customerName;

    private String idCard;

    private String loanBank;

    private String orderId;

    private String operationExceptionTime;

    private Byte carType;

    private Long partnerId;

    private Byte status;

    private Integer pageIndex = 1;

    private Integer pageSize = 20;
}
