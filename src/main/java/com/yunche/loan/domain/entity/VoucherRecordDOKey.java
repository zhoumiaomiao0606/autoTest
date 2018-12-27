package com.yunche.loan.domain.entity;

public class VoucherRecordDOKey {
    private Long orderId;

    private String operationNum;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOperationNum() {
        return operationNum;
    }

    public void setOperationNum(String operationNum) {
        this.operationNum = operationNum == null ? null : operationNum.trim();
    }
}