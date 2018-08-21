package com.yunche.loan.domain.entity;

public class VehicleHandleDOKey
{
    public VehicleHandleDOKey()
    {
    }

    public VehicleHandleDOKey(Long orderid, Long bankRepayImpRecordId)
    {
        this.orderid = orderid;
        this.bankRepayImpRecordId = bankRepayImpRecordId;
    }

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