package com.yunche.loan.domain.entity;

import java.util.Date;

public class LoanRepayPlanDOKey {
    private Long orderId;

    private Date repayDate;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getRepayDate() {
        return repayDate;
    }

    public void setRepayDate(Date repayDate) {
        this.repayDate = repayDate;
    }
}