package com.yunche.loan.domain.entity;

public class FileInfoDO {
    private Long id;

    private String type;

    private Long orderId;

    private Long bankRepayImpRecordId;

    private String remark;

    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getBankRepayImpRecordId() {
        return bankRepayImpRecordId;
    }

    public void setBankRepayImpRecordId(Long bankRepayImpRecordId) {
        this.bankRepayImpRecordId = bankRepayImpRecordId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }
}