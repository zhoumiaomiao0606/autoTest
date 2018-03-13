package com.yunche.loan.domain.entity;

import java.util.Date;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicle_identification_number() {
        return vehicle_identification_number;
    }

    public void setVehicle_identification_number(String vehicle_identification_number) {
        this.vehicle_identification_number = vehicle_identification_number == null ? null : vehicle_identification_number.trim();
    }

    public String getLicense_plate_number() {
        return license_plate_number;
    }

    public void setLicense_plate_number(String license_plate_number) {
        this.license_plate_number = license_plate_number == null ? null : license_plate_number.trim();
    }

    public Date getApply_license_plate_date() {
        return apply_license_plate_date;
    }

    public void setApply_license_plate_date(Date apply_license_plate_date) {
        this.apply_license_plate_date = apply_license_plate_date;
    }

    public String getEngine_number() {
        return engine_number;
    }

    public void setEngine_number(String engine_number) {
        this.engine_number = engine_number == null ? null : engine_number.trim();
    }

    public Byte getLicense_plate_type() {
        return license_plate_type;
    }

    public void setLicense_plate_type(Byte license_plate_type) {
        this.license_plate_type = license_plate_type;
    }

    public String getApply_license_plate_area() {
        return apply_license_plate_area;
    }

    public void setApply_license_plate_area(String apply_license_plate_area) {
        this.apply_license_plate_area = apply_license_plate_area == null ? null : apply_license_plate_area.trim();
    }

    public String getRegistration_certificate_number() {
        return registration_certificate_number;
    }

    public void setRegistration_certificate_number(String registration_certificate_number) {
        this.registration_certificate_number = registration_certificate_number == null ? null : registration_certificate_number.trim();
    }

    public String getQualified_certificate_number() {
        return qualified_certificate_number;
    }

    public void setQualified_certificate_number(String qualified_certificate_number) {
        this.qualified_certificate_number = qualified_certificate_number == null ? null : qualified_certificate_number.trim();
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model == null ? null : car_model.trim();
    }

    public Date getTransfer_ownership_date() {
        return transfer_ownership_date;
    }

    public void setTransfer_ownership_date(Date transfer_ownership_date) {
        this.transfer_ownership_date = transfer_ownership_date;
    }

    public Date getLicense_plate_readiness_date() {
        return license_plate_readiness_date;
    }

    public void setLicense_plate_readiness_date(Date license_plate_readiness_date) {
        this.license_plate_readiness_date = license_plate_readiness_date;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }
}