package com.yunche.loan.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

public class BankLendRecordDO {
    private Integer id;

    private Long loanOrder;

    private Date lendDate;

    private BigDecimal lendAmount;

    private Byte status;

    private Byte recordStatus;

    private Date gmtCreate;

    private String feature;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getLoanOrder() {
        return loanOrder;
    }

    public void setLoanOrder(Long loanOrder) {
        this.loanOrder = loanOrder;
    }

    public Date getLendDate() {
        return lendDate;
    }

    public void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }

    public BigDecimal getLendAmount() {
        return lendAmount;
    }

    public void setLendAmount(BigDecimal lendAmount) {
        this.lendAmount = lendAmount;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(Byte recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }
}