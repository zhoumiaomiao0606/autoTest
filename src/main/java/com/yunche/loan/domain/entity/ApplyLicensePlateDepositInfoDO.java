package com.yunche.loan.domain.entity;

import java.util.Date;

public class ApplyLicensePlateDepositInfoDO {
    private Long id;

    private Date apply_license_plate_deposit_date;

    private String registration_certificate_number;

    private Byte status;

    private String feature;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getApply_license_plate_deposit_date() {
        return apply_license_plate_deposit_date;
    }

    public void setApply_license_plate_deposit_date(Date apply_license_plate_deposit_date) {
        this.apply_license_plate_deposit_date = apply_license_plate_deposit_date;
    }

    public String getRegistration_certificate_number() {
        return registration_certificate_number;
    }

    public void setRegistration_certificate_number(String registration_certificate_number) {
        this.registration_certificate_number = registration_certificate_number == null ? null : registration_certificate_number.trim();
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