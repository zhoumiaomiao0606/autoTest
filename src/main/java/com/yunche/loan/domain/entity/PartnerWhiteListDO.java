package com.yunche.loan.domain.entity;

import java.util.Date;

public class PartnerWhiteListDO extends PartnerWhiteListDOKey {
    private Byte whiteLevel;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    public Byte getWhiteLevel() {
        return whiteLevel;
    }

    public void setWhiteLevel(Byte whiteLevel) {
        this.whiteLevel = whiteLevel;
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

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }
}