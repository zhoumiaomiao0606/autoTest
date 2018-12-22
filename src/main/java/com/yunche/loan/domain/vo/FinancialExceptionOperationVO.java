package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class FinancialExceptionOperationVO
{
    private String orderId;

    private String operationExceptionTime;

    private String customerName;

    private String idCard;

    private String bank;

    private String carType;

    private String partnerName;

    private String operationStatus;

    private String operationExceptionReason;
}
