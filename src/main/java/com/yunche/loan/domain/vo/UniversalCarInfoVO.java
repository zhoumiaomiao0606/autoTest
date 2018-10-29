package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalCarInfoVO {

    private String car_assess_price;
    private String car_brand_name;    //车辆品牌
    private String car_price;         // 车辆价格
    private String actual_car_price;  // 实际成交价格
    private String car_name;//车系
    private String car_detail_name;//车型
    private String car_detail_id;//车型ID
    private String car_vehicle_property;//车辆属性
    private String car_type;//车辆类别
    private String car_key;//是否留备钥匙
    private String needCollectKey;//待收钥匙
    private String vehicle_vehicle_identification_number;
    private String vehicle_license_plate_number;
    private String vehicle_engine_number;
    private String vehicle_apply_license_plate_area;
    private String vehicle_registration_certificate_number;
    private String vehicle_color;//车辆颜色
    private String financial_appraisal;
    private String qualified_certificate_number;
    private String vehicle_customize_brand;
    private String vehicle_purchase_car_invoice_price;
    private String vehicle_invoice_down_payment;
    private String vehicle_purchase_car_invoice_date;
    private String vehicle_invoice_car_dealer;
    private String vehicle_displacement;
    private String vehicle_register_date;
    private String car_category;
    private String vehicle_now_driving_license_owner;
    private String vehicle_assess_use_year;
    private String vehicle_car_category;

}
