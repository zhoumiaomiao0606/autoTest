package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanFinancialPlanDO {
    private Long id;

    private BigDecimal carPrice;

    private Long bizModelId;

    private String bank;

    private BigDecimal signRate;

    private String loanAmount;

    private Integer loanTime;

    private BigDecimal downPaymentRatio;

    private BigDecimal downPaymentMoney;

    private BigDecimal bankPeriodPrincipal;

    private BigDecimal bankFee;

    private BigDecimal principalInterestSum;

    private BigDecimal firstMonthRepay;

    private BigDecimal eachMonthRepay;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;
}