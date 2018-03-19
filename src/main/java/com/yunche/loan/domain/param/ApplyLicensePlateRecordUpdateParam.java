package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class ApplyLicensePlateRecordUpdateParam {
    @NotBlank
    private String order_id;//订单号
    @NotBlank
    private String vehicle_identification_number;// 车架号
    @NotBlank
    private String license_plate_number;//  车牌号
    @NotBlank
    private String apply_license_plate_date;//  上牌日期
    @NotBlank
    private String engine_number;//  发动机号
    @NotBlank
    private String license_plate_type;//  牌证类型 牌证类型 1 公牌 2 私牌
    @NotBlank
    private String apply_license_plate_area_id;//  上牌地
    @NotBlank
    private String registration_certificate_number;//  登记证书号
    @NotBlank
    private String qualified_certificate_number;//  合格证号
    @NotBlank
    private String car_model;//  汽车品牌
    @NotBlank
    private String transfer_ownership_date;//  过户日期
    @NotBlank
    private String license_plate_readiness_date;//  牌证齐全日期

}
