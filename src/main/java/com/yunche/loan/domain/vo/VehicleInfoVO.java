package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 18:37
 * @description:贷后车辆信息
 **/
@Data
public class VehicleInfoVO
{
    //车型  例如 奥迪
    private String car_name;

    //行驶证车主
    private String now_driving_license_owner;

    // 车辆属性
    private String vehicle_property;

    // 车辆类别 -- 1.新车  2.二手车  3.不限
    private String car_type;

    //车辆颜色
    private String color;

    // 上牌方式 1.公牌  2.私牌
    private String license_plate_type;

    //上牌地点id
    private String apply_license_plate_area;

    //发动机号
    private String engine_number;

    //登记证书号
    private String registration_certificate_number;

    //车架号
    private String vehicle_identification_number;

    //车辆型号  --- 合格证号
    private String qualified_certificate_number;

    //购车发票价
    private String purchase_car_invoice_price;

}
