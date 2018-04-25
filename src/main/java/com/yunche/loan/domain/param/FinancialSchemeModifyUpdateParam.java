package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class FinancialSchemeModifyUpdateParam {

    private String his_id;

    @NotBlank
    private String order_id;

    @NotBlank
    private String financial_product_id;

    @NotBlank
    private String financial_appraisal;

    @NotBlank
    private String financial_category_superior;

    @NotBlank
    private String financial_bank;

    @NotBlank
    private String financial_loan_time;

    @NotBlank
    private String financial_down_payment_ratio;

    @NotBlank
    private String financial_bank_fee;

    @NotBlank
    private String financial_product_name;

    @NotBlank
    private String financial_sign_rate;

    @NotBlank
    private String financial_loan_amount;

    @NotBlank
    private String financial_first_month_repay;

    @NotBlank
    private String financial_car_price;

    @NotBlank
    private String financial_down_payment_money;

    @NotBlank
    private String financial_bank_period_principal;

    @NotBlank
    private String financial_each_month_repay;

    @NotBlank
    private String financial_total_repayment_amount;
}