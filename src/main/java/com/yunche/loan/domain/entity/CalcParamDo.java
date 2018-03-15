package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalcParamDo {
    //银行分期本金
    private BigDecimal bankPeriodPrincipal;
    //还款总额
    private BigDecimal totalRepayment;
    //首月还款
    private BigDecimal firstRepayment;
    //银行分期比例
    private BigDecimal stagingRatio;
    //银行手续费
    private BigDecimal bankFee;
    //月还款
    private BigDecimal eachMonthRepay;
    //贷款利息
    private BigDecimal  loanInterest;
    //贷款成数
    private BigDecimal  loanToValueRatio;
    //本金首月还款
    private BigDecimal  principalFirstMonthRepay;
    //本金月还款
    private BigDecimal  principalEachMonthRepay;
    //银行首月手续费
    private BigDecimal  firstMonthBankFee;
    //银行每月手续费
    private BigDecimal  eachMonthBankFee;

}
