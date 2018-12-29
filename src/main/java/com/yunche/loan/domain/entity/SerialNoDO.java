package com.yunche.loan.domain.entity;

import java.util.Date;

public class SerialNoDO extends SerialNoDOKey {
    private Integer operation;

    private String exMessage;

    private Byte status;

    private Date gmtCreate;

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public String getExMessage() {
        return exMessage;
    }

    public void setExMessage(String exMessage) {
        this.exMessage = exMessage == null ? null : exMessage.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}