package com.yunche.loan.domain.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/4
 */
@Data
public class LoanFinancialPlanParam {
    /**
     * 业务单ID
     */
    private Long orderId;
    /**
     * 银行费率
     */
    private BigDecimal bankRate;

    private Long id;

    private BigDecimal carPrice;

    private Long financialProductId;

    private String financialProductName;

    private String bank;

    private BigDecimal signRate;

    /**
     * 实际贷款额
     */
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

    private BigDecimal cashDeposit;

    private BigDecimal extraFee;

    private BigDecimal appraisal;
}
