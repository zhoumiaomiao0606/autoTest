package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

@Data
public class VehicleInformationUpdateParam {

    @NotBlank
    private String order_id;//业务编号
    @NotBlank
    private String license_plate_type;

    private String color;
    @NotBlank
    private String qualified_certificate_number;
//    @NotBlank
    private String register_date;
    @NotBlank
    private String vehicle_identification_number;
    @NotBlank
    private String displacement;

    private String license_plate_number;

    private String apply_license_plate_date;

    private String apply_license_plate_area;

    private String transfer_ownership_date;
    @NotBlank
    private String now_driving_license_owner;

    private String old_driving_license_owner;
    @NotBlank
    private String engine_number;

    private String registration_certificate_number;
    @NotBlank
    private String invoice_car_dealer;
    @NotBlank
    private String purchase_car_invoice_num;
    @NotBlank
    private String purchase_car_invoice_price;
    @NotBlank
    private String invoice_down_payment;
    @NotBlank
    private String purchase_car_invoice_date;
    private String retrieve_key;
    private String customize_brand;

    private String assess_org;

    private String assess_use_year;

    private List<UniversalFileParam> files;
}
