package com.yunche.loan.domain.entity;

import java.util.Date;

public class LoanInfoSupplementDO {
    private Long id;

    private Long orderId;

    private Byte type;

    private String content;

    private String info;

    private String originTask;

    private Long initiatorId;

    private String initiatorName;

    private Date startTime;

    private Long supplementerId;

    private String supplementerName;

    private Date endTime;

    private Byte status;

    private String remark;

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

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }

    public String getOriginTask() {
        return originTask;
    }

    public void setOriginTask(String originTask) {
        this.originTask = originTask == null ? null : originTask.trim();
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName == null ? null : initiatorName.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Long getSupplementerId() {
        return supplementerId;
    }

    public void setSupplementerId(Long supplementerId) {
        this.supplementerId = supplementerId;
    }

    public String getSupplementerName() {
        return supplementerName;
    }

    public void setSupplementerName(String supplementerName) {
        this.supplementerName = supplementerName == null ? null : supplementerName.trim();
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
}