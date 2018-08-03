package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VehicleInformationDO {
    private Long id;

    private Byte license_plate_type;

    private String color;

    private String qualified_certificate_number;

    private Date register_date;

    private String vehicle_identification_number;

    private String displacement;

    private String license_plate_number;

    private Date apply_license_plate_date;

    private String apply_license_plate_area;

    private Date transfer_ownership_date;

    private String now_driving_license_owner;

    private String old_driving_license_owner;

    private String engine_number;

    private String registration_certificate_number;

    private String invoice_car_dealer;

    private String purchase_car_invoice_num;

    private BigDecimal purchase_car_invoice_price;

    private BigDecimal invoice_down_payment;

    private Date purchase_car_invoice_date;

    private Byte retrieve_key;

    private String customize_brand;

    private Byte status;

    private String feature;

    private Long order_id;

    private String assess_org;

    private String assess_use_year;

    private String car_category;
}