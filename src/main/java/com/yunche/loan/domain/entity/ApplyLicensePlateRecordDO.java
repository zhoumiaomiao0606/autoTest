package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ApplyLicensePlateRecordDO {
    private Long id;

    private String vehicle_identification_number;

    private String license_plate_number;

    private Date apply_license_plate_date;

    private String engine_number;

    private Byte license_plate_type;

    private String apply_license_plate_area;

    private String registration_certificate_number;

    private String qualified_certificate_number;

    private String car_model;

    private Date transfer_ownership_date;

    private Date license_plate_readiness_date;

    private Byte status;

    private String feature;

}