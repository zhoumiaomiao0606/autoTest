package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class LoanTaskAgencyDOKey {
    private Long orderId;

    private String taskDefinitionKey;

    private Byte status;
}