package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class LoanFinancialPlanTempHisDO {
    private Long id;

    private Long order_id;

    private Long financial_product_id;

    private BigDecimal financial_appraisal;

    private String financial_category_superior;

    private String financial_bank;

    private Integer financial_loan_time;

    private BigDecimal financial_down_payment_ratio;

    private BigDecimal financial_bank_fee;

    private String financial_product_name;

    private BigDecimal financial_sign_rate;

    private BigDecimal financial_loan_amount;

    private BigDecimal financial_first_month_repay;

    private BigDecimal financial_car_price;

    private BigDecimal financial_down_payment_money;

    private BigDecimal financial_bank_period_principal;

    private BigDecimal financial_each_month_repay;

    private BigDecimal financial_total_repayment_amount;

    private Long initiator_id;

    private String initiator_name;

    private Timestamp start_time;

    private Timestamp end_time;

    private Byte status;
}