package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UniversalInfoVO {

    private String order_id;
    // 预计贷款额
    private String expect_loan_amount;
    private String overdue_amount;
    private String material_complete_material_date;
    private String material_remark;
    private String material_contract_num;
    private String loan_gmt_create;
    private String product_category_superior;
    private String department_name;
    private String customer_id;
    private String customer_id_card;
    private String customer_name;
    private String customer_address;
    private String customer_income_certificate_company_name;
    private String customer_income_certificate_company_address;
    private String customer_postcode;
    private String customer_company_name;
    private String customer_company_address;
    private String customer_working_years;
    private String customer_mobile;
    private String customer_company_phone;
    private String customer_birth;
    private String customer_sex;
    private String customer_age;
    private String customer_month_income;
    private String customer_education;
    private String customer_duty;
    private String customer_identity_address;
    private String customer_residence_address;
    private String customer_family_person_num;
    private String customer_reserve_mobile;
    private String customer_cprovince;
    private String customer_ccity;
    private String customer_ccounty;
    private String customer_hprovince;
    private String customer_hcity;
    private String customer_hcounty;
    private String customer_lend_card;
    private String customer_signature_type;
    private String customer_open_card_status;
    private String partner_id;
    private String partner_name;
    private String partner_code;
    private String partner_group;
    private String partner_biz_area;
    private String partner_risk_bear_rate;
    private String partner_pay_month;
    private String salesman_id;
    private String salesman_name;
    private String salesman_mobile;
    private String car_cooperation_dealer;
    private String car_detail_id;
    private String car_name;
    private String car_type;
    private String car_gps_num;
    private String car_business_source;
    private String car_vehicle_property;
    private String car_key;
    private String car_distributor_name;

    private String vehicle_color;
    private String vehicle_now_driving_license_owner;
    private String vehicle_license_plate_type;
    private String vehicle_apply_license_plate_area_id;
    private String vehicle_apply_license_plate_area;
    private String vehicle_old_driving_license_owner;
    private String bank_credit_gmt_create;
    private String society_credit_gmt_create;
    private Long financial_id;
    private String financial_bank;
    private String financial_loan_time;
    private String financial_appraisal;
    private String financial_down_payment_ratio;
    private String financial_bank_rate;
    private String financial_loan_ratio;
    private String financial_bank_fee;
    private String financial_product_id;
    private String financial_product_name;
    private String financial_sign_rate;
    private String financial_loan_amount;
    private String financial_first_month_repay;
    private String financial_bank_staging_ratio;
    private String financial_cash_deposit;
    private String financial_car_price;
    private String financial_actual_car_price;
    private String financial_down_payment_money;
    private String financial_bank_period_principal;
    private String financial_each_month_repay;
    private String financial_total_repayment_amount;
    private String financial_extra_fee;
    private String financial_padding_company;
    private String financial_play_company;
    private String remit_beneficiary_bank;
    private String remit_beneficiary_account;
    private String remit_beneficiary_account_number;
    private String remit_amount;
    private String remit_return_rate_amount;
    private String pay_month;

    private String process_apply_license_plate_deposit_info;
    private String process_bank_lend_record;
    private String process_video_review;// 视频审核

    private String car_info;
    private String bank_repay_date;
    private String bank_billing_date;
    private String car_category;
    private BigDecimal performance_fee;
    private String cprovince;
    private String ccity;
    private String ccounty;
    private String hprovince;
    private String hcity;
    private String hcounty;
    private String remit_review_time;
    private String bank_id;

    private String customer_ctelzone;
    private String remit_application_date;
    private String bridge_lend_date;
    private String bank_lend_date;


    private BigDecimal riskBearRate;
}
