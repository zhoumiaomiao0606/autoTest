package com.yunche.loan.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

public class LoanApplyCompensationDO {
    private Long id;

    private Long orderId;

    private BigDecimal currArrears;

    private BigDecimal loanBanlance;

    private BigDecimal advancesBanlance;

    private Integer overdueDays;

    private Integer overdueNumber;

    private Integer advancesNumber;

    private BigDecimal riskTakingRatio;

    private BigDecimal compensationAmount;

    private String compensationCause;

    private Date applyCompensationDate;

    private String outCarNumber;

    private String outBank;

    private String outAccount;

    private String receiveBank;

    private String receiveCarNumber;

    private String receiveAccount;

    private Date reviewDate;

    private String reviewOperator;

    private String partnerCompensationOperator;

    private Date partnerOperationDate;

    private BigDecimal partnerCompensationAmount;

    private String partnerDcReviewOperator;

    private Date partnerDcReviewDate;

    private Byte status;

    private String remark;

    private Date gmtCreate;

    private Date gmtModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getCurrArrears() {
        return currArrears;
    }

    public void setCurrArrears(BigDecimal currArrears) {
        this.currArrears = currArrears;
    }

    public BigDecimal getLoanBanlance() {
        return loanBanlance;
    }

    public void setLoanBanlance(BigDecimal loanBanlance) {
        this.loanBanlance = loanBanlance;
    }

    public BigDecimal getAdvancesBanlance() {
        return advancesBanlance;
    }

    public void setAdvancesBanlance(BigDecimal advancesBanlance) {
        this.advancesBanlance = advancesBanlance;
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public Integer getOverdueNumber() {
        return overdueNumber;
    }

    public void setOverdueNumber(Integer overdueNumber) {
        this.overdueNumber = overdueNumber;
    }

    public Integer getAdvancesNumber() {
        return advancesNumber;
    }

    public void setAdvancesNumber(Integer advancesNumber) {
        this.advancesNumber = advancesNumber;
    }

    public BigDecimal getRiskTakingRatio() {
        return riskTakingRatio;
    }

    public void setRiskTakingRatio(BigDecimal riskTakingRatio) {
        this.riskTakingRatio = riskTakingRatio;
    }

    public BigDecimal getCompensationAmount() {
        return compensationAmount;
    }

    public void setCompensationAmount(BigDecimal compensationAmount) {
        this.compensationAmount = compensationAmount;
    }

    public String getCompensationCause() {
        return compensationCause;
    }

    public void setCompensationCause(String compensationCause) {
        this.compensationCause = compensationCause == null ? null : compensationCause.trim();
    }

    public Date getApplyCompensationDate() {
        return applyCompensationDate;
    }

    public void setApplyCompensationDate(Date applyCompensationDate) {
        this.applyCompensationDate = applyCompensationDate;
    }

    public String getOutCarNumber() {
        return outCarNumber;
    }

    public void setOutCarNumber(String outCarNumber) {
        this.outCarNumber = outCarNumber == null ? null : outCarNumber.trim();
    }

    public String getOutBank() {
        return outBank;
    }

    public void setOutBank(String outBank) {
        this.outBank = outBank == null ? null : outBank.trim();
    }

    public String getOutAccount() {
        return outAccount;
    }

    public void setOutAccount(String outAccount) {
        this.outAccount = outAccount == null ? null : outAccount.trim();
    }

    public String getReceiveBank() {
        return receiveBank;
    }

    public void setReceiveBank(String receiveBank) {
        this.receiveBank = receiveBank == null ? null : receiveBank.trim();
    }

    public String getReceiveCarNumber() {
        return receiveCarNumber;
    }

    public void setReceiveCarNumber(String receiveCarNumber) {
        this.receiveCarNumber = receiveCarNumber == null ? null : receiveCarNumber.trim();
    }

    public String getReceiveAccount() {
        return receiveAccount;
    }

    public void setReceiveAccount(String receiveAccount) {
        this.receiveAccount = receiveAccount == null ? null : receiveAccount.trim();
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewOperator() {
        return reviewOperator;
    }

    public void setReviewOperator(String reviewOperator) {
        this.reviewOperator = reviewOperator == null ? null : reviewOperator.trim();
    }

    public String getPartnerCompensationOperator() {
        return partnerCompensationOperator;
    }

    public void setPartnerCompensationOperator(String partnerCompensationOperator) {
        this.partnerCompensationOperator = partnerCompensationOperator == null ? null : partnerCompensationOperator.trim();
    }

    public Date getPartnerOperationDate() {
        return partnerOperationDate;
    }

    public void setPartnerOperationDate(Date partnerOperationDate) {
        this.partnerOperationDate = partnerOperationDate;
    }

    public BigDecimal getPartnerCompensationAmount() {
        return partnerCompensationAmount;
    }

    public void setPartnerCompensationAmount(BigDecimal partnerCompensationAmount) {
        this.partnerCompensationAmount = partnerCompensationAmount;
    }

    public String getPartnerDcReviewOperator() {
        return partnerDcReviewOperator;
    }

    public void setPartnerDcReviewOperator(String partnerDcReviewOperator) {
        this.partnerDcReviewOperator = partnerDcReviewOperator == null ? null : partnerDcReviewOperator.trim();
    }

    public Date getPartnerDcReviewDate() {
        return partnerDcReviewDate;
    }

    public void setPartnerDcReviewDate(Date partnerDcReviewDate) {
        this.partnerDcReviewDate = partnerDcReviewDate;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }
}