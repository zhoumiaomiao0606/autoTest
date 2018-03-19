package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ApplyLicensePlateRecordVO {

    private String order_id;//业务编号
    private String customer_id;//主贷人id
    private String cname;//主贷人姓名
    private String id_card;//证件号
    private String ename;//业务员
    private String pname;//合伙人
    private String car_type;//购车类型 车辆类型：1-新车; 2-二手车; 3-不限;
    private String vehicle_identification_number;//车架号
    private String license_plate_number;//车牌号
    private String apply_license_plate_date;//上牌日期
    private String engine_number;//发动机号
    private String license_plate_type;//牌证类型 牌证类型 1 公牌 2 私牌
    private String apply_license_plate_area_id;//上牌地
    private String apply_license_plate_parent_area_id;//上牌地
    private String registration_certificate_number;//登记证书号
    private String qualified_certificate_number;//合格证类型
    private String car_model;//汽车品牌
    private String transfer_ownership_date;//过户日期
    private String license_plate_readiness_date;//牌证齐全日期
}
