package com.yunche.loan.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

public class MaterialAuditDO {
    private Long id;

    private Date complete_material_date;

    private Byte rate_type;

    private Byte is_subsidy;

    private Byte is_pledge;

    private Byte is_guarantee;

    private BigDecimal rate;

    private Byte status;

    private String feature;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getComplete_material_date() {
        return complete_material_date;
    }

    public void setComplete_material_date(Date complete_material_date) {
        this.complete_material_date = complete_material_date;
    }

    public Byte getRate_type() {
        return rate_type;
    }

    public void setRate_type(Byte rate_type) {
        this.rate_type = rate_type;
    }

    public Byte getIs_subsidy() {
        return is_subsidy;
    }

    public void setIs_subsidy(Byte is_subsidy) {
        this.is_subsidy = is_subsidy;
    }

    public Byte getIs_pledge() {
        return is_pledge;
    }

    public void setIs_pledge(Byte is_pledge) {
        this.is_pledge = is_pledge;
    }

    public Byte getIs_guarantee() {
        return is_guarantee;
    }

    public void setIs_guarantee(Byte is_guarantee) {
        this.is_guarantee = is_guarantee;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
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