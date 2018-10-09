package com.yunche.loan.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

public class RemitDetailsDO {
    private Long id;

    private String beneficiary_bank;

    private String beneficiary_account;

    private String beneficiary_account_number;

    private BigDecimal remit_amount;

    private BigDecimal return_rate_amount;

    private String insurance_situation;

    private String remark;

    private String payment_organization;

    private Date application_date;

    private Date remit_time;

    private Date gmt_create;

    private Date gmt_modify;

    private Byte status;

    private String feature;

    private String remit_bank;

    private String remit_account;

    private String remit_account_number;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBeneficiary_bank() {
        return beneficiary_bank;
    }

    public void setBeneficiary_bank(String beneficiary_bank) {
        this.beneficiary_bank = beneficiary_bank == null ? null : beneficiary_bank.trim();
    }

    public String getBeneficiary_account() {
        return beneficiary_account;
    }

    public void setBeneficiary_account(String beneficiary_account) {
        this.beneficiary_account = beneficiary_account == null ? null : beneficiary_account.trim();
    }

    public String getBeneficiary_account_number() {
        return beneficiary_account_number;
    }

    public void setBeneficiary_account_number(String beneficiary_account_number) {
        this.beneficiary_account_number = beneficiary_account_number == null ? null : beneficiary_account_number.trim();
    }

    public BigDecimal getRemit_amount() {
        return remit_amount;
    }

    public void setRemit_amount(BigDecimal remit_amount) {
        this.remit_amount = remit_amount;
    }

    public BigDecimal getReturn_rate_amount() {
        return return_rate_amount;
    }

    public void setReturn_rate_amount(BigDecimal return_rate_amount) {
        this.return_rate_amount = return_rate_amount;
    }

    public String getInsurance_situation() {
        return insurance_situation;
    }

    public void setInsurance_situation(String insurance_situation) {
        this.insurance_situation = insurance_situation == null ? null : insurance_situation.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getPayment_organization() {
        return payment_organization;
    }

    public void setPayment_organization(String payment_organization) {
        this.payment_organization = payment_organization == null ? null : payment_organization.trim();
    }

    public Date getApplication_date() {
        return application_date;
    }

    public void setApplication_date(Date application_date) {
        this.application_date = application_date;
    }

    public Date getRemit_time() {
        return remit_time;
    }

    public void setRemit_time(Date remit_time) {
        this.remit_time = remit_time;
    }

    public Date getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(Date gmt_create) {
        this.gmt_create = gmt_create;
    }

    public Date getGmt_modify() {
        return gmt_modify;
    }

    public void setGmt_modify(Date gmt_modify) {
        this.gmt_modify = gmt_modify;
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

    public String getRemit_bank() {
        return remit_bank;
    }

    public void setRemit_bank(String remit_bank) {
        this.remit_bank = remit_bank == null ? null : remit_bank.trim();
    }

    public String getRemit_account() {
        return remit_account;
    }

    public void setRemit_account(String remit_account) {
        this.remit_account = remit_account == null ? null : remit_account.trim();
    }

    public String getRemit_account_number() {
        return remit_account_number;
    }

    public void setRemit_account_number(String remit_account_number) {
        this.remit_account_number = remit_account_number == null ? null : remit_account_number.trim();
    }
}