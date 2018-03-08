package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.BaseVO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Data
public class AppLoanFinancialPlanParam {
    /**
     * 业务单ID
     */
    private Long orderId;

    private Long id;

    private BigDecimal carPrice;
    /**
     * 金融产品
     */
    private BaseVO flinancialProduct;

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
}
