package com.yunche.loan.domain.entity;

import java.util.Date;

public class ForceDO {
    private Long id;

    private Date registerDate;

    private String registerId;

    private String summary;

    private String ruleCourt;

    private String executionApplicant;

    private String executor;

    private String undertakeJudge;

    private String undertakeTarget;

    private String propertyClues;

    private Date suspensionDate;

    private Date endDate;

    private String courtAcceptTarget;

    private String undertakeRemarks;

    private String repaidNum;

    private String repaidMoney;

    private String repaidInterest;

    private String surplusNum;

    private String surplusMoney;

    private String surplusInterest;

    private Date secondDate;

    private String secondTarget;

    private String secondRemarks;

    private String forceRemarks;

    private Long orderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId == null ? null : registerId.trim();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    public String getRuleCourt() {
        return ruleCourt;
    }

    public void setRuleCourt(String ruleCourt) {
        this.ruleCourt = ruleCourt == null ? null : ruleCourt.trim();
    }

    public String getExecutionApplicant() {
        return executionApplicant;
    }

    public void setExecutionApplicant(String executionApplicant) {
        this.executionApplicant = executionApplicant == null ? null : executionApplicant.trim();
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor == null ? null : executor.trim();
    }

    public String getUndertakeJudge() {
        return undertakeJudge;
    }

    public void setUndertakeJudge(String undertakeJudge) {
        this.undertakeJudge = undertakeJudge == null ? null : undertakeJudge.trim();
    }

    public String getUndertakeTarget() {
        return undertakeTarget;
    }

    public void setUndertakeTarget(String undertakeTarget) {
        this.undertakeTarget = undertakeTarget == null ? null : undertakeTarget.trim();
    }

    public String getPropertyClues() {
        return propertyClues;
    }

    public void setPropertyClues(String propertyClues) {
        this.propertyClues = propertyClues == null ? null : propertyClues.trim();
    }

    public Date getSuspensionDate() {
        return suspensionDate;
    }

    public void setSuspensionDate(Date suspensionDate) {
        this.suspensionDate = suspensionDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCourtAcceptTarget() {
        return courtAcceptTarget;
    }

    public void setCourtAcceptTarget(String courtAcceptTarget) {
        this.courtAcceptTarget = courtAcceptTarget == null ? null : courtAcceptTarget.trim();
    }

    public String getUndertakeRemarks() {
        return undertakeRemarks;
    }

    public void setUndertakeRemarks(String undertakeRemarks) {
        this.undertakeRemarks = undertakeRemarks == null ? null : undertakeRemarks.trim();
    }

    public String getRepaidNum() {
        return repaidNum;
    }

    public void setRepaidNum(String repaidNum) {
        this.repaidNum = repaidNum == null ? null : repaidNum.trim();
    }

    public String getRepaidMoney() {
        return repaidMoney;
    }

    public void setRepaidMoney(String repaidMoney) {
        this.repaidMoney = repaidMoney == null ? null : repaidMoney.trim();
    }

    public String getRepaidInterest() {
        return repaidInterest;
    }

    public void setRepaidInterest(String repaidInterest) {
        this.repaidInterest = repaidInterest == null ? null : repaidInterest.trim();
    }

    public String getSurplusNum() {
        return surplusNum;
    }

    public void setSurplusNum(String surplusNum) {
        this.surplusNum = surplusNum == null ? null : surplusNum.trim();
    }

    public String getSurplusMoney() {
        return surplusMoney;
    }

    public void setSurplusMoney(String surplusMoney) {
        this.surplusMoney = surplusMoney == null ? null : surplusMoney.trim();
    }

    public String getSurplusInterest() {
        return surplusInterest;
    }

    public void setSurplusInterest(String surplusInterest) {
        this.surplusInterest = surplusInterest == null ? null : surplusInterest.trim();
    }

    public Date getSecondDate() {
        return secondDate;
    }

    public void setSecondDate(Date secondDate) {
        this.secondDate = secondDate;
    }

    public String getSecondTarget() {
        return secondTarget;
    }

    public void setSecondTarget(String secondTarget) {
        this.secondTarget = secondTarget == null ? null : secondTarget.trim();
    }

    public String getSecondRemarks() {
        return secondRemarks;
    }

    public void setSecondRemarks(String secondRemarks) {
        this.secondRemarks = secondRemarks == null ? null : secondRemarks.trim();
    }

    public String getForceRemarks() {
        return forceRemarks;
    }

    public void setForceRemarks(String forceRemarks) {
        this.forceRemarks = forceRemarks == null ? null : forceRemarks.trim();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}