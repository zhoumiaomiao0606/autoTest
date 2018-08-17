package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.BaseAreaDO;
import lombok.Data;

@Data
public class VehicleInformationVO {

    //订单号
    private String order_id;
    //贷款客户ud
    private String customer_id;
    //身份证号
    private String id_card;
    //手机号
    private String mobilenumber;
    //主贷人
    private String cname;

    // 车辆类别 -- 1.新车  2.二手车  3.不限
    private String car_type;

    // 车辆属性 --国产
    private String vehicle_property;
    // 车价格
    private String car_price;
    // 实际成交价格
    private String actual_car_price;

    // 基准评估价
    private String assess_price;

    //车型  例如 奥迪
    private String car_name;

    //汽车品牌
    private String customize_brand;

    //上牌方式 1.公牌  2.私牌
    private String license_plate_type;

    //车辆颜色
    private String color;

    //注册日期
    private String register_date;

    //车架号
    private String vehicle_identification_number;

    //排量
    private String displacement;

    //车牌号
    private String license_plate_number;

    //上牌时间
    private String apply_license_plate_date;

    //上牌地点id
    private String apply_license_plate_area;

    //上牌地点名称
   /* private String apply_license_plate_area_name;*/

    //过户日期
    private String transfer_ownership_date;

    //行驶证车主
    private String now_driving_license_owner;

    //原行驶证车主
    private String old_driving_license_owner;

    //发动机号
    private String engine_number;

    //登记证书号
    private String registration_certificate_number;

    //发票车商
    private String invoice_car_dealer;

    //购车发票号
    private String purchase_car_invoice_num;

    //购车发票价
    private String purchase_car_invoice_price;

    //发票首付款
    private String invoice_down_payment;

    //购车发票时间
    private String purchase_car_invoice_date;

    //使用年限
    private String assess_use_year;

    private String ename;
    private String pname;


    private String bank;
    private String bank_period_principal;
    private String loan_amount;

    private String production_type;

    //车辆类型  例如  汽车  小轿车
    private String car_category;


    //车辆型号  --- 合格证号
    private String qualified_certificate_number;


    private String retrieve_key;
    private String down_payment_money;
    private String first_register_date;

    private BaseAreaDO hasApplyLicensePlateArea;

    private String assess_org;

}
