package com.yunche.loan.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

public class LoanRepayPlanDO extends LoanRepayPlanDOKey {
    private BigDecimal payableAmount;

    private BigDecimal actualRepayAmount;

    private Byte isOverdue;

    private BigDecimal overdueAmount;

    private Date checkDate;

    private Byte status;

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public BigDecimal getActualRepayAmount() {
        return actualRepayAmount;
    }

    public void setActualRepayAmount(BigDecimal actualRepayAmount) {
        this.actualRepayAmount = actualRepayAmount;
    }

    public Byte getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Byte isOverdue) {
        this.isOverdue = isOverdue;
    }

    public BigDecimal getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(BigDecimal overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}