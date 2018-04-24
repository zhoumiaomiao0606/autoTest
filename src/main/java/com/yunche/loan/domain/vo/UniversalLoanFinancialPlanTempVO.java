package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UniversalLoanFinancialPlanTempVO {

    private String order_id;

    private String financial_product_id;

    private String financial_appraisal;

    private String financial_category_superior;

    private String financial_bank;

    private String financial_loan_time;

    private String financial_down_payment_ratio;

    private String financial_bank_fee;

    private String financial_product_name;

    private String financial_sign_rate;

    private String financial_loan_amount;

    private String financial_first_month_repay;

    private String financial_car_price;

    private String financial_down_payment_money;

    private String financial_bank_period_principal;

    private String financial_each_month_repay;

    private String financial_total_repayment_amount;

    private String gmt_create;
}
