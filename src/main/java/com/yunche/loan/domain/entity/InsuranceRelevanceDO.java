package com.yunche.loan.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

public class InsuranceRelevanceDO {
    private Long id;

    private Long insurance_info_id;

    private Long insurance_company_name;

    private String insurance_number;

    private String area;

    private BigDecimal insurance_amount;

    private Date start_date;

    private Date end_date;

    private Byte insurance_type;

    private Byte status;

    private String feature;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInsurance_info_id() {
        return insurance_info_id;
    }

    public void setInsurance_info_id(Long insurance_info_id) {
        this.insurance_info_id = insurance_info_id;
    }

    public Long getInsurance_company_name() {
        return insurance_company_name;
    }

    public void setInsurance_company_name(Long insurance_company_name) {
        this.insurance_company_name = insurance_company_name;
    }

    public String getInsurance_number() {
        return insurance_number;
    }

    public void setInsurance_number(String insurance_number) {
        this.insurance_number = insurance_number == null ? null : insurance_number.trim();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    public BigDecimal getInsurance_amount() {
        return insurance_amount;
    }

    public void setInsurance_amount(BigDecimal insurance_amount) {
        this.insurance_amount = insurance_amount;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Byte getInsurance_type() {
        return insurance_type;
    }

    public void setInsurance_type(Byte insurance_type) {
        this.insurance_type = insurance_type;
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