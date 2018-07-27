package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ApplyDiviGeneralInfoVO {

    private String car_type;  //车辆类型 车辆类型：1-新车; 2-二手车; 3-不限;
    private String customer_name;   //客户名称
    private String customer_id_card;    //身份证号
    private String customer_mobile;     //手机号
    private String customer_family_address;//家庭地址
    private String customer_income_certificate_company_name;//收入证明单位
    private String car_name;//车辆名称
    private String financial_car_price;//车价
    private String vehicle_identification_number;//车架号
    private String vehicle_registration_certificate_number;//注册登记号
    private String vehicle_license_plate_number;//车牌号
    private String car_assess_price;//品估计
    private String vehicle_use_year;//使用年限
    private String financial_down_payment_money;//首付款
    private String financial_bank_period_principal;//分期本金
    private String financial_loan_time;//分期时间
    private String financial_bank_fee;//手续费
    private String financial_bank_staging_ratio;//分期比例
    private String financial_bank_rate;//分期费率
    private String is_pledge;//是否抵押 0否 1 是
    private String customer_lend_card;//信用卡卡号
    private String customer_collateral;//抵押物
    private String vehicle_assess_org;


}
