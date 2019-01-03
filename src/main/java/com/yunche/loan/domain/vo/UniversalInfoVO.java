package com.yunche.loan.domain.vo;

import java.math.BigDecimal;


public class UniversalInfoVO {
    private String overdue_amount;
    private String order_id;
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


    public String getOverdue_amount() {
        return overdue_amount;
    }

    public void setOverdue_amount(String overdue_amount) {
        this.overdue_amount = overdue_amount;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getMaterial_complete_material_date() {
        return material_complete_material_date;
    }

    public void setMaterial_complete_material_date(String material_complete_material_date) {
        this.material_complete_material_date = material_complete_material_date;
    }

    public String getMaterial_remark() {
        return material_remark;
    }

    public void setMaterial_remark(String material_remark) {
        this.material_remark = material_remark;
    }

    public String getMaterial_contract_num() {
        return material_contract_num;
    }

    public void setMaterial_contract_num(String material_contract_num) {
        this.material_contract_num = material_contract_num;
    }

    public String getLoan_gmt_create() {
        return loan_gmt_create;
    }

    public void setLoan_gmt_create(String loan_gmt_create) {
        this.loan_gmt_create = loan_gmt_create;
    }

    public String getProduct_category_superior() {
        return product_category_superior;
    }

    public void setProduct_category_superior(String product_category_superior) {
        this.product_category_superior = product_category_superior;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_id_card() {
        return customer_id_card;
    }

    public void setCustomer_id_card(String customer_id_card) {
        this.customer_id_card = customer_id_card;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getCustomer_income_certificate_company_name() {
        return customer_income_certificate_company_name;
    }

    public void setCustomer_income_certificate_company_name(String customer_income_certificate_company_name) {
        this.customer_income_certificate_company_name = customer_income_certificate_company_name;
    }

    public String getCustomer_income_certificate_company_address() {
        return customer_income_certificate_company_address;
    }

    public void setCustomer_income_certificate_company_address(String customer_income_certificate_company_address) {
        this.customer_income_certificate_company_address = customer_income_certificate_company_address;
    }

    public String getCustomer_postcode() {
        return customer_postcode;
    }

    public void setCustomer_postcode(String customer_postcode) {
        this.customer_postcode = customer_postcode;
    }

    public String getCustomer_company_name() {
        return customer_company_name;
    }

    public void setCustomer_company_name(String customer_company_name) {
        this.customer_company_name = customer_company_name;
    }

    public String getCustomer_company_address() {
        return customer_company_address;
    }

    public void setCustomer_company_address(String customer_company_address) {
        this.customer_company_address = customer_company_address;
    }

    public String getCustomer_working_years() {
        return customer_working_years;
    }

    public void setCustomer_working_years(String customer_working_years) {
        this.customer_working_years = customer_working_years;
    }

    public String getCustomer_mobile() {
        return customer_mobile;
    }

    public void setCustomer_mobile(String customer_mobile) {
        this.customer_mobile = customer_mobile;
    }

    public String getCustomer_company_phone() {
        return customer_company_phone;
    }

    public void setCustomer_company_phone(String customer_company_phone) {
        this.customer_company_phone = customer_company_phone;
    }

    public String getCustomer_birth() {
        return customer_birth;
    }

    public void setCustomer_birth(String customer_birth) {
        this.customer_birth = customer_birth;
    }

    public String getCustomer_sex() {
        return customer_sex;
    }

    public void setCustomer_sex(String customer_sex) {
        this.customer_sex = customer_sex;
    }

    public String getCustomer_age() {
        return customer_age;
    }

    public void setCustomer_age(String customer_age) {
        this.customer_age = customer_age;
    }

    public String getCustomer_month_income() {
        return customer_month_income;
    }

    public void setCustomer_month_income(String customer_month_income) {
        this.customer_month_income = customer_month_income;
    }

    public String getCustomer_education() {
        return customer_education;
    }

    public void setCustomer_education(String customer_education) {
        this.customer_education = customer_education;
    }

    public String getCustomer_duty() {
        return customer_duty;
    }

    public void setCustomer_duty(String customer_duty) {
        this.customer_duty = customer_duty;
    }

    public String getCustomer_identity_address() {
        return customer_identity_address;
    }

    public void setCustomer_identity_address(String customer_identity_address) {
        this.customer_identity_address = customer_identity_address;
    }

    public String getCustomer_residence_address() {
        return customer_residence_address;
    }

    public void setCustomer_residence_address(String customer_residence_address) {
        this.customer_residence_address = customer_residence_address;
    }

    public String getCustomer_family_person_num() {
        return customer_family_person_num;
    }

    public void setCustomer_family_person_num(String customer_family_person_num) {
        this.customer_family_person_num = customer_family_person_num;
    }

    public String getCustomer_reserve_mobile() {
        return customer_reserve_mobile;
    }

    public void setCustomer_reserve_mobile(String customer_reserve_mobile) {
        this.customer_reserve_mobile = customer_reserve_mobile;
    }

    public String getCustomer_cprovince() {
        return customer_cprovince;
    }

    public void setCustomer_cprovince(String customer_cprovince) {
        this.customer_cprovince = customer_cprovince;
    }

    public String getCustomer_ccity() {
        return customer_ccity;
    }

    public void setCustomer_ccity(String customer_ccity) {
        this.customer_ccity = customer_ccity;
    }

    public String getCustomer_ccounty() {
        return customer_ccounty;
    }

    public void setCustomer_ccounty(String customer_ccounty) {
        this.customer_ccounty = customer_ccounty;
    }

    public String getCustomer_hprovince() {
        return customer_hprovince;
    }

    public void setCustomer_hprovince(String customer_hprovince) {
        this.customer_hprovince = customer_hprovince;
    }

    public String getCustomer_hcity() {
        return customer_hcity;
    }

    public void setCustomer_hcity(String customer_hcity) {
        this.customer_hcity = customer_hcity;
    }

    public String getCustomer_hcounty() {
        return customer_hcounty;
    }

    public void setCustomer_hcounty(String customer_hcounty) {
        this.customer_hcounty = customer_hcounty;
    }

    public String getCustomer_lend_card() {
        return customer_lend_card;
    }

    public void setCustomer_lend_card(String customer_lend_card) {
        this.customer_lend_card = customer_lend_card;
    }

    public String getCustomer_signature_type() {
        return customer_signature_type;
    }

    public void setCustomer_signature_type(String customer_signature_type) {
        this.customer_signature_type = customer_signature_type;
    }

    public String getCustomer_open_card_status() {
        return customer_open_card_status;
    }

    public void setCustomer_open_card_status(String customer_open_card_status) {
        this.customer_open_card_status = customer_open_card_status;
    }

    public String getPartner_id() {
        return partner_id;
    }

    public void setPartner_id(String partner_id) {
        this.partner_id = partner_id;
    }

    public String getPartner_name() {
        return partner_name;
    }

    public void setPartner_name(String partner_name) {
        this.partner_name = partner_name;
    }

    public String getPartner_code() {
        return partner_code;
    }

    public void setPartner_code(String partner_code) {
        this.partner_code = partner_code;
    }

    public String getPartner_group() {
        return partner_group;
    }

    public void setPartner_group(String partner_group) {
        this.partner_group = partner_group;
    }

    public String getPartner_biz_area() {
        return partner_biz_area;
    }

    public void setPartner_biz_area(String partner_biz_area) {
        this.partner_biz_area = partner_biz_area;
    }

    public String getPartner_risk_bear_rate() {
        return partner_risk_bear_rate;
    }

    public void setPartner_risk_bear_rate(String partner_risk_bear_rate) {
        this.partner_risk_bear_rate = partner_risk_bear_rate;
    }

    public String getPartner_pay_month() {
        return partner_pay_month;
    }

    public void setPartner_pay_month(String partner_pay_month) {
        this.partner_pay_month = partner_pay_month;
    }

    public String getSalesman_id() {
        return salesman_id;
    }

    public void setSalesman_id(String salesman_id) {
        this.salesman_id = salesman_id;
    }

    public String getSalesman_name() {
        return salesman_name;
    }

    public void setSalesman_name(String salesman_name) {
        this.salesman_name = salesman_name;
    }

    public String getSalesman_mobile() {
        return salesman_mobile;
    }

    public void setSalesman_mobile(String salesman_mobile) {
        this.salesman_mobile = salesman_mobile;
    }

    public String getCar_cooperation_dealer() {
        return car_cooperation_dealer;
    }

    public void setCar_cooperation_dealer(String car_cooperation_dealer) {
        this.car_cooperation_dealer = car_cooperation_dealer;
    }

    public String getCar_detail_id() {
        return car_detail_id;
    }

    public void setCar_detail_id(String car_detail_id) {
        this.car_detail_id = car_detail_id;
    }

    public String getCar_name() {
        return car_name;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }

    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getCar_gps_num() {
        return car_gps_num;
    }

    public void setCar_gps_num(String car_gps_num) {
        this.car_gps_num = car_gps_num;
    }

    public String getCar_business_source() {
        return car_business_source;
    }

    public void setCar_business_source(String car_business_source) {
        this.car_business_source = car_business_source;
    }

    public String getCar_vehicle_property() {
        return car_vehicle_property;
    }

    public void setCar_vehicle_property(String car_vehicle_property) {
        this.car_vehicle_property = car_vehicle_property;
    }

    public String getCar_key() {
        return car_key;
    }

    public void setCar_key(String car_key) {
        this.car_key = car_key;
    }

    public String getVehicle_color() {
        return vehicle_color;
    }

    public void setVehicle_color(String vehicle_color) {
        this.vehicle_color = vehicle_color;
    }

    public String getVehicle_now_driving_license_owner() {
        return vehicle_now_driving_license_owner;
    }

    public void setVehicle_now_driving_license_owner(String vehicle_now_driving_license_owner) {
        this.vehicle_now_driving_license_owner = vehicle_now_driving_license_owner;
    }

    public String getVehicle_license_plate_type() {
        return vehicle_license_plate_type;
    }

    public void setVehicle_license_plate_type(String vehicle_license_plate_type) {
        this.vehicle_license_plate_type = vehicle_license_plate_type;
    }

    public String getVehicle_apply_license_plate_area_id() {
        return vehicle_apply_license_plate_area_id;
    }

    public void setVehicle_apply_license_plate_area_id(String vehicle_apply_license_plate_area_id) {
        this.vehicle_apply_license_plate_area_id = vehicle_apply_license_plate_area_id;
    }

    public String getVehicle_apply_license_plate_area() {
        return vehicle_apply_license_plate_area;
    }

    public void setVehicle_apply_license_plate_area(String vehicle_apply_license_plate_area) {
        this.vehicle_apply_license_plate_area = vehicle_apply_license_plate_area;
    }

    public String getVehicle_old_driving_license_owner() {
        return vehicle_old_driving_license_owner;
    }

    public void setVehicle_old_driving_license_owner(String vehicle_old_driving_license_owner) {
        this.vehicle_old_driving_license_owner = vehicle_old_driving_license_owner;
    }

    public String getBank_credit_gmt_create() {
        return bank_credit_gmt_create;
    }

    public void setBank_credit_gmt_create(String bank_credit_gmt_create) {
        this.bank_credit_gmt_create = bank_credit_gmt_create;
    }

    public String getSociety_credit_gmt_create() {
        return society_credit_gmt_create;
    }

    public void setSociety_credit_gmt_create(String society_credit_gmt_create) {
        this.society_credit_gmt_create = society_credit_gmt_create;
    }

    public Long getFinancial_id() {
        return financial_id;
    }

    public void setFinancial_id(Long financial_id) {
        this.financial_id = financial_id;
    }

    public String getFinancial_bank() {
        return financial_bank;
    }

    public void setFinancial_bank(String financial_bank) {
        this.financial_bank = financial_bank;
    }

    public String getFinancial_loan_time() {
        return financial_loan_time;
    }

    public void setFinancial_loan_time(String financial_loan_time) {
        this.financial_loan_time = financial_loan_time;
    }

    public String getFinancial_appraisal() {
        return financial_appraisal;
    }

    public void setFinancial_appraisal(String financial_appraisal) {
        this.financial_appraisal = financial_appraisal;
    }

    public String getFinancial_down_payment_ratio() {
        return financial_down_payment_ratio;
    }

    public void setFinancial_down_payment_ratio(String financial_down_payment_ratio) {
        this.financial_down_payment_ratio = financial_down_payment_ratio;
    }

    public String getFinancial_bank_rate() {
        return financial_bank_rate;
    }

    public void setFinancial_bank_rate(String financial_bank_rate) {
        this.financial_bank_rate = financial_bank_rate;
    }

    public String getFinancial_loan_ratio() {
        return financial_loan_ratio;
    }

    public void setFinancial_loan_ratio(String financial_loan_ratio) {
        this.financial_loan_ratio = financial_loan_ratio;
    }

    public String getFinancial_bank_fee() {
        return financial_bank_fee;
    }

    public void setFinancial_bank_fee(String financial_bank_fee) {
        this.financial_bank_fee = financial_bank_fee;
    }

    public String getFinancial_product_id() {
        return financial_product_id;
    }

    public void setFinancial_product_id(String financial_product_id) {
        this.financial_product_id = financial_product_id;
    }

    public String getFinancial_product_name() {
        return financial_product_name;
    }

    public void setFinancial_product_name(String financial_product_name) {
        this.financial_product_name = financial_product_name;
    }

    public String getFinancial_sign_rate() {
        return financial_sign_rate;
    }

    public void setFinancial_sign_rate(String financial_sign_rate) {
        this.financial_sign_rate = financial_sign_rate;
    }

    public String getFinancial_loan_amount() {
        return financial_loan_amount;
    }

    public void setFinancial_loan_amount(String financial_loan_amount) {
        this.financial_loan_amount = financial_loan_amount;
    }

    public String getFinancial_first_month_repay() {
        return financial_first_month_repay;
    }

    public void setFinancial_first_month_repay(String financial_first_month_repay) {
        this.financial_first_month_repay = financial_first_month_repay;
    }

    public String getFinancial_bank_staging_ratio() {
        return financial_bank_staging_ratio;
    }

    public void setFinancial_bank_staging_ratio(String financial_bank_staging_ratio) {
        this.financial_bank_staging_ratio = financial_bank_staging_ratio;
    }

    public String getFinancial_cash_deposit() {
        return financial_cash_deposit;
    }

    public void setFinancial_cash_deposit(String financial_cash_deposit) {
        this.financial_cash_deposit = financial_cash_deposit;
    }

    public String getFinancial_car_price() {
        return financial_car_price;
    }

    public void setFinancial_car_price(String financial_car_price) {
        this.financial_car_price = financial_car_price;
    }

    public String getFinancial_actual_car_price() {
        return financial_actual_car_price;
    }

    public void setFinancial_actual_car_price(String financial_actual_car_price) {
        this.financial_actual_car_price = financial_actual_car_price;
    }

    public String getFinancial_down_payment_money() {
        return financial_down_payment_money;
    }

    public void setFinancial_down_payment_money(String financial_down_payment_money) {
        this.financial_down_payment_money = financial_down_payment_money;
    }

    public String getFinancial_bank_period_principal() {
        return financial_bank_period_principal;
    }

    public void setFinancial_bank_period_principal(String financial_bank_period_principal) {
        this.financial_bank_period_principal = financial_bank_period_principal;
    }

    public String getFinancial_each_month_repay() {
        return financial_each_month_repay;
    }

    public void setFinancial_each_month_repay(String financial_each_month_repay) {
        this.financial_each_month_repay = financial_each_month_repay;
    }

    public String getFinancial_total_repayment_amount() {
        return financial_total_repayment_amount;
    }

    public void setFinancial_total_repayment_amount(String financial_total_repayment_amount) {
        this.financial_total_repayment_amount = financial_total_repayment_amount;
    }

    public String getFinancial_extra_fee() {
        return financial_extra_fee;
    }

    public void setFinancial_extra_fee(String financial_extra_fee) {
        this.financial_extra_fee = financial_extra_fee;
    }

    public String getFinancial_padding_company() {
        return financial_padding_company;
    }

    public void setFinancial_padding_company(String financial_padding_company) {
        this.financial_padding_company = financial_padding_company;
    }

    public String getFinancial_play_company() {
        return financial_play_company;
    }

    public void setFinancial_play_company(String financial_play_company) {
        this.financial_play_company = financial_play_company;
    }

    public String getRemit_beneficiary_bank() {
        return remit_beneficiary_bank;
    }

    public void setRemit_beneficiary_bank(String remit_beneficiary_bank) {
        this.remit_beneficiary_bank = remit_beneficiary_bank;
    }

    public String getRemit_beneficiary_account() {
        return remit_beneficiary_account;
    }

    public void setRemit_beneficiary_account(String remit_beneficiary_account) {
        this.remit_beneficiary_account = remit_beneficiary_account;
    }

    public String getRemit_beneficiary_account_number() {
        return remit_beneficiary_account_number;
    }

    public void setRemit_beneficiary_account_number(String remit_beneficiary_account_number) {
        this.remit_beneficiary_account_number = remit_beneficiary_account_number;
    }

    public String getRemit_amount() {
        return remit_amount;
    }

    public void setRemit_amount(String remit_amount) {
        this.remit_amount = remit_amount;
    }

    public String getRemit_return_rate_amount() {
        return remit_return_rate_amount;
    }

    public void setRemit_return_rate_amount(String remit_return_rate_amount) {
        this.remit_return_rate_amount = remit_return_rate_amount;
    }

    public String getPay_month() {
        return pay_month;
    }

    public void setPay_month(String pay_month) {
        this.pay_month = pay_month;
    }

    public String getProcess_apply_license_plate_deposit_info() {
        return process_apply_license_plate_deposit_info;
    }

    public void setProcess_apply_license_plate_deposit_info(String process_apply_license_plate_deposit_info) {
        this.process_apply_license_plate_deposit_info = process_apply_license_plate_deposit_info;
    }

    public String getProcess_bank_lend_record() {
        return process_bank_lend_record;
    }

    public void setProcess_bank_lend_record(String process_bank_lend_record) {
        this.process_bank_lend_record = process_bank_lend_record;
    }

    public String getProcess_video_review() {
        return process_video_review;
    }

    public void setProcess_video_review(String process_video_review) {
        this.process_video_review = process_video_review;
    }

    public String getCar_info() {
        return car_info;
    }

    public void setCar_info(String car_info) {
        this.car_info = car_info;
    }

    public String getBank_repay_date() {
        return bank_repay_date;
    }

    public void setBank_repay_date(String bank_repay_date) {
        this.bank_repay_date = bank_repay_date;
    }

    public String getBank_billing_date() {
        return bank_billing_date;
    }

    public void setBank_billing_date(String bank_billing_date) {
        this.bank_billing_date = bank_billing_date;
    }

    public String getCar_category() {
        return car_category;
    }

    public void setCar_category(String car_category) {
        this.car_category = car_category;
    }

    public BigDecimal getPerformance_fee() {
        return performance_fee;
    }

    public void setPerformance_fee(BigDecimal performance_fee) {
        this.performance_fee = performance_fee;
    }

    public String getCprovince() {
        return cprovince;
    }

    public void setCprovince(String cprovince) {
        this.cprovince = cprovince;
    }

    public String getCcity() {
        return ccity;
    }

    public void setCcity(String ccity) {
        this.ccity = ccity;
    }

    public String getCcounty() {
        return ccounty;
    }

    public void setCcounty(String ccounty) {
        this.ccounty = ccounty;
    }

    public String getHprovince() {
        return hprovince;
    }

    public void setHprovince(String hprovince) {
        this.hprovince = hprovince;
    }

    public String getHcity() {
        return hcity;
    }

    public void setHcity(String hcity) {
        this.hcity = hcity;
    }

    public String getHcounty() {
        return hcounty;
    }

    public void setHcounty(String hcounty) {
        this.hcounty = hcounty;
    }

    public String getRemit_review_time() {
        return remit_review_time;
    }

    public void setRemit_review_time(String remit_review_time) {
        this.remit_review_time = remit_review_time;
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public String getCustomer_ctelzone() {
        return customer_ctelzone;
    }

    public void setCustomer_ctelzone(String customer_ctelzone) {
        this.customer_ctelzone = customer_ctelzone;
    }

    public String getRemit_application_date() {
        return remit_application_date;
    }

    public void setRemit_application_date(String remit_application_date) {
        this.remit_application_date = remit_application_date;
    }

    public String getBridge_lend_date() {
        return bridge_lend_date;
    }

    public void setBridge_lend_date(String bridge_lend_date) {
        this.bridge_lend_date = bridge_lend_date;
    }

    public String getBank_lend_date() {
        return bank_lend_date;
    }

    public void setBank_lend_date(String bank_lend_date) {
        this.bank_lend_date = bank_lend_date;
    }

    public BigDecimal getRiskBearRate() {
        return riskBearRate;
    }

    public void setRiskBearRate(BigDecimal riskBearRate) {
        this.riskBearRate = riskBearRate;
    }

    public String getCar_distributor_name() {
        return car_distributor_name;
    }

    public void setCar_distributor_name(String car_distributor_name) {
        this.car_distributor_name = car_distributor_name;
    }
}
