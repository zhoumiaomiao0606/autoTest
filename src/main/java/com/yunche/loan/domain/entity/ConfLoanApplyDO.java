package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfLoanApplyDO extends ConfLoanApplyDOKey {
    private BigDecimal down_payment_ratio;

    private BigDecimal loan_ratio;

    private BigDecimal financial_service_fee;

    private BigDecimal staging_ratio;

    private String down_payment_ratio_compare;

    private String loan_ratio_compare;

    private String staging_ratio_compard;

    private String financial_service_fee_compard;
}