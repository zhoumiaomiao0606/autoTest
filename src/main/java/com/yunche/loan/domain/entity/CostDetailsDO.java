package com.yunche.loan.domain.entity;

import java.math.BigDecimal;

public class CostDetailsDO {
    private Long id;

    private BigDecimal service_fee;

    private BigDecimal apply_license_plate_deposit_fee;

    private BigDecimal performance_fee;

    private BigDecimal install_gps_fee;

    private BigDecimal risk_fee;

    private BigDecimal fair_assess_fee;

    private BigDecimal apply_license_plate_out_province_fee;

    private BigDecimal based_margin_fee;

    private BigDecimal service_fee_type;

    private Byte apply_license_plate_deposit_fee_type;

    private Byte performance_fee_type;

    private Byte install_gps_fee_type;

    private Byte risk_fee_type;

    private Byte fair_assess_fee_type;

    private Byte apply_license_plate_out_province_fee_type;

    private Byte based_margin_fee_type;

    private Byte status;

    private String feature;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getService_fee() {
        return service_fee;
    }

    public void setService_fee(BigDecimal service_fee) {
        this.service_fee = service_fee;
    }

    public BigDecimal getApply_license_plate_deposit_fee() {
        return apply_license_plate_deposit_fee;
    }

    public void setApply_license_plate_deposit_fee(BigDecimal apply_license_plate_deposit_fee) {
        this.apply_license_plate_deposit_fee = apply_license_plate_deposit_fee;
    }

    public BigDecimal getPerformance_fee() {
        return performance_fee;
    }

    public void setPerformance_fee(BigDecimal performance_fee) {
        this.performance_fee = performance_fee;
    }

    public BigDecimal getInstall_gps_fee() {
        return install_gps_fee;
    }

    public void setInstall_gps_fee(BigDecimal install_gps_fee) {
        this.install_gps_fee = install_gps_fee;
    }

    public BigDecimal getRisk_fee() {
        return risk_fee;
    }

    public void setRisk_fee(BigDecimal risk_fee) {
        this.risk_fee = risk_fee;
    }

    public BigDecimal getFair_assess_fee() {
        return fair_assess_fee;
    }

    public void setFair_assess_fee(BigDecimal fair_assess_fee) {
        this.fair_assess_fee = fair_assess_fee;
    }

    public BigDecimal getApply_license_plate_out_province_fee() {
        return apply_license_plate_out_province_fee;
    }

    public void setApply_license_plate_out_province_fee(BigDecimal apply_license_plate_out_province_fee) {
        this.apply_license_plate_out_province_fee = apply_license_plate_out_province_fee;
    }

    public BigDecimal getBased_margin_fee() {
        return based_margin_fee;
    }

    public void setBased_margin_fee(BigDecimal based_margin_fee) {
        this.based_margin_fee = based_margin_fee;
    }

    public BigDecimal getService_fee_type() {
        return service_fee_type;
    }

    public void setService_fee_type(BigDecimal service_fee_type) {
        this.service_fee_type = service_fee_type;
    }

    public Byte getApply_license_plate_deposit_fee_type() {
        return apply_license_plate_deposit_fee_type;
    }

    public void setApply_license_plate_deposit_fee_type(Byte apply_license_plate_deposit_fee_type) {
        this.apply_license_plate_deposit_fee_type = apply_license_plate_deposit_fee_type;
    }

    public Byte getPerformance_fee_type() {
        return performance_fee_type;
    }

    public void setPerformance_fee_type(Byte performance_fee_type) {
        this.performance_fee_type = performance_fee_type;
    }

    public Byte getInstall_gps_fee_type() {
        return install_gps_fee_type;
    }

    public void setInstall_gps_fee_type(Byte install_gps_fee_type) {
        this.install_gps_fee_type = install_gps_fee_type;
    }

    public Byte getRisk_fee_type() {
        return risk_fee_type;
    }

    public void setRisk_fee_type(Byte risk_fee_type) {
        this.risk_fee_type = risk_fee_type;
    }

    public Byte getFair_assess_fee_type() {
        return fair_assess_fee_type;
    }

    public void setFair_assess_fee_type(Byte fair_assess_fee_type) {
        this.fair_assess_fee_type = fair_assess_fee_type;
    }

    public Byte getApply_license_plate_out_province_fee_type() {
        return apply_license_plate_out_province_fee_type;
    }

    public void setApply_license_plate_out_province_fee_type(Byte apply_license_plate_out_province_fee_type) {
        this.apply_license_plate_out_province_fee_type = apply_license_plate_out_province_fee_type;
    }

    public Byte getBased_margin_fee_type() {
        return based_margin_fee_type;
    }

    public void setBased_margin_fee_type(Byte based_margin_fee_type) {
        this.based_margin_fee_type = based_margin_fee_type;
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