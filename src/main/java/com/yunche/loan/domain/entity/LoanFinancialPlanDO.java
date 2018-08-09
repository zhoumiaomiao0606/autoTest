package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanFinancialPlanDO {

    private Long id;

    private BigDecimal carPrice;

    private BigDecimal actualCarPrice;

    private Long financialProductId;

    private String financialProductName;

    private String bank;

    private BigDecimal signRate;

    private BigDecimal loanAmount;

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

    private BigDecimal cashDeposit;

    private BigDecimal extraFee;

    private BigDecimal appraisal;

    private String paddingCompany;//垫资平台

    private String playCompany;//打款平台
}