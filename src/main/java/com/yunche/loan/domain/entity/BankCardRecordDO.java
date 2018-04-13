package com.yunche.loan.domain.entity;

import java.util.Date;

public class BankCardRecordDO {
    private Integer id;

    private Long orderId;

    private String userName;

    private String idCard;

    private String billingDate;

    private Date firstBillingDate;

    private String repayDate;

    private Date firstRepaymentDate;

    private String repayCardId;

    private Date receiveDate;

    private String sendee;

    private Byte status;

    private String feature;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? null : idCard.trim();
    }

    public String getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(String billingDate) {
        this.billingDate = billingDate == null ? null : billingDate.trim();
    }

    public Date getFirstBillingDate() {
        return firstBillingDate;
    }

    public void setFirstBillingDate(Date firstBillingDate) {
        this.firstBillingDate = firstBillingDate;
    }

    public String getRepayDate() {
        return repayDate;
    }

    public void setRepayDate(String repayDate) {
        this.repayDate = repayDate == null ? null : repayDate.trim();
    }

    public Date getFirstRepaymentDate() {
        return firstRepaymentDate;
    }

    public void setFirstRepaymentDate(Date firstRepaymentDate) {
        this.firstRepaymentDate = firstRepaymentDate;
    }

    public String getRepayCardId() {
        return repayCardId;
    }

    public void setRepayCardId(String repayCardId) {
        this.repayCardId = repayCardId == null ? null : repayCardId.trim();
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getSendee() {
        return sendee;
    }

    public void setSendee(String sendee) {
        this.sendee = sendee == null ? null : sendee.trim();
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