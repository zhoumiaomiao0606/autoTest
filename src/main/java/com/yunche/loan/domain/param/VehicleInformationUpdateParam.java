package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class VehicleInformationUpdateParam {

    //业务编号

    private String order_id;

    private Long second_hand_car_vin_id;

    //上牌方式

    private String license_plate_type;

    //车辆颜色
    private String color;

    //合格证号==车辆型号

    private String qualified_certificate_number;

    //注册日期
//    @NotBlank
    private String register_date;

    //车架号

    private String vehicle_identification_number;
    //排量

    private String displacement;

    //车牌号
    private String license_plate_number;

    //上牌时间
    private String apply_license_plate_date;

    //上牌地点
    private String apply_license_plate_area;

    //过户日期
    private String transfer_ownership_date;

    //行驶证车主

    private String now_driving_license_owner;

    //原行驶证车主
    private String old_driving_license_owner;

    //发动机号

    private String engine_number;


    //登记书号
    private String registration_certificate_number;

    //发票车商

    private String invoice_car_dealer;

    //购车发票号

    private String purchase_car_invoice_num;

    //购车发票价格

    private String purchase_car_invoice_price;

    //发票首付款

    private String invoice_down_payment;

    //购车发票日期

    private String purchase_car_invoice_date;

    private String retrieve_key;


    //汽车品牌
    private String customize_brand;

    private String assess_org;

    //使用年限
    private String assess_use_year;

    //车辆类型
    private String car_category;

    //附件路径
    private List<UniversalFileParam> files;
}
