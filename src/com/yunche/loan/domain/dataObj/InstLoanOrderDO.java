package com.yunche.loan.domain.dataObj;

import java.util.Date;

public class InstLoanOrderDO {
    private Long orderId;

    private Long custId;

    private Long prodId;

    private String processInstId;

    private Long carId;

    private Byte carType;

    private Integer carPrice;

    private Byte carKey;

    private Integer gpsNum;

    private Integer interestRate;

    private Integer loanAmount;

    private Integer firstPayAmount;

    private Integer loanRate;

    private Integer loanStage;

    private Integer bankPrincipalAmount;

    private Integer bankChargeAmount;

    private Integer totalAmount;

    private Integer firstMonthPayAmount;

    private Integer eachMonthPayAmount;

    private Long partnerId;

    private String partnerAccountName;

    private String partnerBank;

    private String partnerAccountNum;

    private Integer partnerPayType;

    private Long insuId;

    private Long salesmanId;

    private String salesmanName;

    private Byte amountGrade;

    private Long areaId;

    private String prov;

    private String city;

    private String feature;

    private String bank;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public String getProcessInstId() {
        return processInstId;
    }

    public void setProcessInstId(String processInstId) {
        this.processInstId = processInstId == null ? null : processInstId.trim();
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Byte getCarType() {
        return carType;
    }

    public void setCarType(Byte carType) {
        this.carType = carType;
    }

    public Integer getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(Integer carPrice) {
        this.carPrice = carPrice;
    }

    public Byte getCarKey() {
        return carKey;
    }

    public void setCarKey(Byte carKey) {
        this.carKey = carKey;
    }

    public Integer getGpsNum() {
        return gpsNum;
    }

    public void setGpsNum(Integer gpsNum) {
        this.gpsNum = gpsNum;
    }

    public Integer getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Integer interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Integer loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Integer getFirstPayAmount() {
        return firstPayAmount;
    }

    public void setFirstPayAmount(Integer firstPayAmount) {
        this.firstPayAmount = firstPayAmount;
    }

    public Integer getLoanRate() {
        return loanRate;
    }

    public void setLoanRate(Integer loanRate) {
        this.loanRate = loanRate;
    }

    public Integer getLoanStage() {
        return loanStage;
    }

    public void setLoanStage(Integer loanStage) {
        this.loanStage = loanStage;
    }

    public Integer getBankPrincipalAmount() {
        return bankPrincipalAmount;
    }

    public void setBankPrincipalAmount(Integer bankPrincipalAmount) {
        this.bankPrincipalAmount = bankPrincipalAmount;
    }

    public Integer getBankChargeAmount() {
        return bankChargeAmount;
    }

    public void setBankChargeAmount(Integer bankChargeAmount) {
        this.bankChargeAmount = bankChargeAmount;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getFirstMonthPayAmount() {
        return firstMonthPayAmount;
    }

    public void setFirstMonthPayAmount(Integer firstMonthPayAmount) {
        this.firstMonthPayAmount = firstMonthPayAmount;
    }

    public Integer getEachMonthPayAmount() {
        return eachMonthPayAmount;
    }

    public void setEachMonthPayAmount(Integer eachMonthPayAmount) {
        this.eachMonthPayAmount = eachMonthPayAmount;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerAccountName() {
        return partnerAccountName;
    }

    public void setPartnerAccountName(String partnerAccountName) {
        this.partnerAccountName = partnerAccountName == null ? null : partnerAccountName.trim();
    }

    public String getPartnerBank() {
        return partnerBank;
    }

    public void setPartnerBank(String partnerBank) {
        this.partnerBank = partnerBank == null ? null : partnerBank.trim();
    }

    public String getPartnerAccountNum() {
        return partnerAccountNum;
    }

    public void setPartnerAccountNum(String partnerAccountNum) {
        this.partnerAccountNum = partnerAccountNum == null ? null : partnerAccountNum.trim();
    }

    public Integer getPartnerPayType() {
        return partnerPayType;
    }

    public void setPartnerPayType(Integer partnerPayType) {
        this.partnerPayType = partnerPayType;
    }

    public Long getInsuId() {
        return insuId;
    }

    public void setInsuId(Long insuId) {
        this.insuId = insuId;
    }

    public Long getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(Long salesmanId) {
        this.salesmanId = salesmanId;
    }

    public String getSalesmanName() {
        return salesmanName;
    }

    public void setSalesmanName(String salesmanName) {
        this.salesmanName = salesmanName == null ? null : salesmanName.trim();
    }

    public Byte getAmountGrade() {
        return amountGrade;
    }

    public void setAmountGrade(Byte amountGrade) {
        this.amountGrade = amountGrade;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov == null ? null : prov.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank == null ? null : bank.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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