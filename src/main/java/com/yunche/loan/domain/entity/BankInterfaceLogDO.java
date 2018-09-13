package com.yunche.loan.domain.entity;

import java.util.Date;

public class BankInterfaceLogDO extends BankInterfaceLogDOKey {
    private String operateName;

    private Date operateDate;

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName == null ? null : operateName.trim();
    }

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }
}