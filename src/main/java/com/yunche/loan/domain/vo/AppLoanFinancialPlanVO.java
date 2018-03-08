package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Data
public class AppLoanFinancialPlanVO {
    private Long id;

    private BigDecimal carPrice;

    /**
     * 金融产品 id & name
     */
    private BaseVO financialProduct;

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
}
