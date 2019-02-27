package com.yunche.loan.domain.entity;

public class DataManagementInfoDO {
    private Long orderId;

    private String contractHandDate;

    private String mortgageHandDate;

    private String mortgageSendDate;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getContractHandDate() {
        return contractHandDate;
    }

    public void setContractHandDate(String contractHandDate) {
        this.contractHandDate = contractHandDate == null ? null : contractHandDate.trim();
    }

    public String getMortgageHandDate() {
        return mortgageHandDate;
    }

    public void setMortgageHandDate(String mortgageHandDate) {
        this.mortgageHandDate = mortgageHandDate == null ? null : mortgageHandDate.trim();
    }

    public String getMortgageSendDate() {
        return mortgageSendDate;
    }

    public void setMortgageSendDate(String mortgageSendDate) {
        this.mortgageSendDate = mortgageSendDate == null ? null : mortgageSendDate.trim();
    }
}