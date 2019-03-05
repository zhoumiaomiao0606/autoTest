package com.yunche.loan.domain.entity;

public class PartnerWhiteListDOKey
{
    private Long partnerId;

    private String operationType;

    public PartnerWhiteListDOKey() {
    }

    public PartnerWhiteListDOKey(Long partnerId, String operationType) {
        this.partnerId = partnerId;
        this.operationType = operationType;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType == null ? null : operationType.trim();
    }
}