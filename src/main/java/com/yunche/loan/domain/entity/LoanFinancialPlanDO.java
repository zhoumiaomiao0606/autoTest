package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanFinancialPlanDO {
    private Long id;

    private BigDecimal carPrice;

    private Long financialProductId;

    private String financialProductName;
    /**
     * 贷款银行名称
     */
    private String bank;
    /**
     * 签约利率
     */
    private BigDecimal signRate;
    /**
     * 实际贷款额
     */
    private BigDecimal loanAmount;
    /**
     * 贷款期数
     */
    private Integer loanTime;
    /**
     * 首付比例
     */
    private BigDecimal downPaymentRatio;
    /**
     * 首付额
     */
    private BigDecimal downPaymentMoney;
    /**
     * 银行分期本金
     */
    private BigDecimal bankPeriodPrincipal;
    /**
     * 银行手续费
     */
    private BigDecimal bankFee;
    /**
     * 本息合计(还款总额)
     */
    private BigDecimal principalInterestSum;
    /**
     * 首月还款
     */
    private BigDecimal firstMonthRepay;
    /**
     * 每月还款
     */
    private BigDecimal eachMonthRepay;
    /**
     * 保证金
     */
    private BigDecimal cashDeposit;
    /**
     * 额外费用
     */
    private BigDecimal extraFee;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;
}