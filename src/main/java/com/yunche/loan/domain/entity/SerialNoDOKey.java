package com.yunche.loan.domain.entity;

public class SerialNoDOKey
{
    public SerialNoDOKey() {
    }

    public SerialNoDOKey(Long orderId, Long serialNo) {
        this.orderId = orderId;
        this.serialNo = serialNo;
    }

    private Long orderId;

    private Long serialNo;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Long serialNo) {
        this.serialNo = serialNo;
    }
}