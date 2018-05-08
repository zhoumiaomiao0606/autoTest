package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class CarUpdateParam {
    @NotBlank
    private String order_id;

    private String car_assess_price;
    private String car_brand_name;
    private String car_price;
    private String car_name;
    private String car_vehicle_property;
    private String car_type;
    private String vehicle_vehicle_identification_number;
    private String vehicle_license_plate_number;
    private String vehicle_engine_number;
    private String vehicle_apply_license_plate_area;
    private String vehicle_registration_certificate_number;
    private String vehicle_color;
    private String financial_appraisal;

}
