package com.yunche.loan.domain.entity;

public class VehicleOutboundDO {
    private Long orderid;

    private String reason;

    private String address;

    private String specificAddress;

    private String customerCondition;

    private String progress;

    private Byte result;

    private String remarks;

    private String balance;

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getSpecificAddress() {
        return specificAddress;
    }

    public void setSpecificAddress(String specificAddress) {
        this.specificAddress = specificAddress == null ? null : specificAddress.trim();
    }

    public String getCustomerCondition() {
        return customerCondition;
    }

    public void setCustomerCondition(String customerCondition) {
        this.customerCondition = customerCondition == null ? null : customerCondition.trim();
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress == null ? null : progress.trim();
    }

    public Byte getResult() {
        return result;
    }

    public void setResult(Byte result) {
        this.result = result;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance == null ? null : balance.trim();
    }
}