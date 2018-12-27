package com.yunche.loan.domain.entity;

import java.util.Date;

public class VoucherRecordDO extends VoucherRecordDOKey {
    private String voucherNum;

    private Date gmtCreate;

    public String getVoucherNum() {
        return voucherNum;
    }

    public void setVoucherNum(String voucherNum) {
        this.voucherNum = voucherNum == null ? null : voucherNum.trim();
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}