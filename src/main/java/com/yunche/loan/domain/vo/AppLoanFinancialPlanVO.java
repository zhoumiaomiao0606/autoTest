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

    private BigDecimal actualCarPrice;

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

    private BigDecimal cashDeposit;

    private BigDecimal extraFee;

    private BigDecimal appraisal;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    /**
     * 银行分期比例
     */
    private BigDecimal stagingRatio;

    /**
     * 产品大类
     */
    private String categorySuperior;

    private BigDecimal bankRate;


    private String paddingCompany;//垫资平台

    private String playCompany;//打款平台

    private String financialServiceFee;//金融服务费

    private String loanRate;//贷款比例


    /**
     * 区域对象
     */
    private BaseVO area;
}
