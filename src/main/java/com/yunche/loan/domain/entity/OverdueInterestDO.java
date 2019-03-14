package com.yunche.loan.domain.entity;

import java.math.BigDecimal;

public class OverdueInterestDO {
    private Long orderId;

    private BigDecimal contractOvedueInterest;

    private String vehicleOvedueInterest;

    private String receiveCompany;

    private String receiveAccount;

    private String info;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getContractOvedueInterest() {
        return contractOvedueInterest;
    }

    public void setContractOvedueInterest(BigDecimal contractOvedueInterest) {
        this.contractOvedueInterest = contractOvedueInterest;
    }

    public String getVehicleOvedueInterest() {
        return vehicleOvedueInterest;
    }

    public void setVehicleOvedueInterest(String vehicleOvedueInterest) {
        this.vehicleOvedueInterest = vehicleOvedueInterest == null ? null : vehicleOvedueInterest.trim();
    }

    public String getReceiveCompany() {
        return receiveCompany;
    }

    public void setReceiveCompany(String receiveCompany) {
        this.receiveCompany = receiveCompany == null ? null : receiveCompany.trim();
    }

    public String getReceiveAccount() {
        return receiveAccount;
    }

    public void setReceiveAccount(String receiveAccount) {
        this.receiveAccount = receiveAccount == null ? null : receiveAccount.trim();
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }
}