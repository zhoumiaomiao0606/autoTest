package com.yunche.loan.domain.entity;

import java.util.Date;

public class InsuranceInfoDO {
    private Long id;

    private Long order_id;

    private Date issue_bills_date;

    private String residential_address;

    private Byte status;

    private String feature;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Date getIssue_bills_date() {
        return issue_bills_date;
    }

    public void setIssue_bills_date(Date issue_bills_date) {
        this.issue_bills_date = issue_bills_date;
    }

    public String getResidential_address() {
        return residential_address;
    }

    public void setResidential_address(String residential_address) {
        this.residential_address = residential_address == null ? null : residential_address.trim();
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