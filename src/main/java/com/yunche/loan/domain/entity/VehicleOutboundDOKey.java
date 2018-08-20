package com.yunche.loan.domain.entity;

public class VehicleOutboundDOKey {
    private Long orderid;

    private Long bankRepayImpRecordId;

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public Long getBankRepayImpRecordId() {
        return bankRepayImpRecordId;
    }

    public void setBankRepayImpRecordId(Long bankRepayImpRecordId) {
        this.bankRepayImpRecordId = bankRepayImpRecordId;
    }
}