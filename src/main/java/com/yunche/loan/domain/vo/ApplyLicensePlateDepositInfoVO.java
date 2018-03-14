package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ApplyLicensePlateDepositInfoVO {
    private String order_id;//业务编号
    private String customer_id;//客户id
    private String cname;//客户名称
    private String id_card;//身份证号码
    private String ename;//业务员
    private String pname;//合伙人
    private String car_type;//购车类型 车辆类型：1-新车; 2-二手车; 3-不限;
    private String license_plate_number;//汽车牌照
    private String bank;//抵押权人
    private String apply_license_plate_deposit_date;//抵押日期
    private String registration_certificate_number;//登记证书号
}
