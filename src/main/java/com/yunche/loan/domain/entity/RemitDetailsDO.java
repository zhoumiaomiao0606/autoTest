package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RemitDetailsDO {
    private Long id;

    private String beneficiary_bank;

    private String beneficiary_account;

    private String beneficiary_account_number;

    private BigDecimal remit_amount;

    private BigDecimal return_rate_amount;

    private Byte status;

    private String feature;
}