package com.yunche.loan.domain.entity;

import java.util.Date;

public class ConfRefundApplyAccountDO {
    private Long id;

    private String refundPayOpenBank;

    private String refundPayAccountName;

    private String refundPayAccount;

    private Date gmtCreate;

    private Date gmtModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefundPayOpenBank() {
        return refundPayOpenBank;
    }

    public void setRefundPayOpenBank(String refundPayOpenBank) {
        this.refundPayOpenBank = refundPayOpenBank == null ? null : refundPayOpenBank.trim();
    }

    public String getRefundPayAccountName() {
        return refundPayAccountName;
    }

    public void setRefundPayAccountName(String refundPayAccountName) {
        this.refundPayAccountName = refundPayAccountName == null ? null : refundPayAccountName.trim();
    }

    public String getRefundPayAccount() {
        return refundPayAccount;
    }

    public void setRefundPayAccount(String refundPayAccount) {
        this.refundPayAccount = refundPayAccount == null ? null : refundPayAccount.trim();
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