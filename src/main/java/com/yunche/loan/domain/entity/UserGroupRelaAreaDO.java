package com.yunche.loan.domain.entity;

import java.util.Date;

public class UserGroupRelaAreaDO extends UserGroupRelaAreaDOKey {
    private Date gmtCreate;

    private Date gmtModify;

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