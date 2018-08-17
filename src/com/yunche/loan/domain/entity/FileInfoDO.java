package com.yunche.loan.domain.entity;

public class FileInfoDO {
    private Long id;

    private String undertakeMoney;

    private String undertakeFine;

    private String undertakeFee;

    private String undertakeInterest;

    private String undertakeTotal;

    private Long orderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUndertakeMoney() {
        return undertakeMoney;
    }

    public void setUndertakeMoney(String undertakeMoney) {
        this.undertakeMoney = undertakeMoney == null ? null : undertakeMoney.trim();
    }

    public String getUndertakeFine() {
        return undertakeFine;
    }

    public void setUndertakeFine(String undertakeFine) {
        this.undertakeFine = undertakeFine == null ? null : undertakeFine.trim();
    }

    public String getUndertakeFee() {
        return undertakeFee;
    }

    public void setUndertakeFee(String undertakeFee) {
        this.undertakeFee = undertakeFee == null ? null : undertakeFee.trim();
    }

    public String getUndertakeInterest() {
        return undertakeInterest;
    }

    public void setUndertakeInterest(String undertakeInterest) {
        this.undertakeInterest = undertakeInterest == null ? null : undertakeInterest.trim();
    }

    public String getUndertakeTotal() {
        return undertakeTotal;
    }

    public void setUndertakeTotal(String undertakeTotal) {
        this.undertakeTotal = undertakeTotal == null ? null : undertakeTotal.trim();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}