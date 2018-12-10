package com.yunche.loan.domain.entity;

import java.util.Date;

public class FinancialOperationExceptionDO {
    private Long id;

    private Long orderId;

    private Byte processSchedule;

    private String operationExceptionParam;

    private Date operationExceptionTime;

    private String operationExceptionReason;

    private Byte status;

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

    public Byte getProcessSchedule() {
        return processSchedule;
    }

    public void setProcessSchedule(Byte processSchedule) {
        this.processSchedule = processSchedule;
    }

    public String getOperationExceptionParam() {
        return operationExceptionParam;
    }

    public void setOperationExceptionParam(String operationExceptionParam) {
        this.operationExceptionParam = operationExceptionParam == null ? null : operationExceptionParam.trim();
    }

    public Date getOperationExceptionTime() {
        return operationExceptionTime;
    }

    public void setOperationExceptionTime(Date operationExceptionTime) {
        this.operationExceptionTime = operationExceptionTime;
    }

    public String getOperationExceptionReason() {
        return operationExceptionReason;
    }

    public void setOperationExceptionReason(String operationExceptionReason) {
        this.operationExceptionReason = operationExceptionReason == null ? null : operationExceptionReason.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}