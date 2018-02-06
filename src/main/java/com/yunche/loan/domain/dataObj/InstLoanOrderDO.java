package com.yunche.loan.domain.dataObj;

import java.io.Serializable;
import java.util.Date;

public class InstLoanOrderDO implements Serializable {
    private Long orderId;

    private Long custId;

    private Long prodId;

    private String processInstId;

    private Integer loanAmount;

    private Integer firstPayAmount;

    private Integer totalPayAmount;

    private Integer firstMonthPayAmount;

    private Integer eachMonthPayAmount;

    private Long carId;

    private Long insuId;

    private Long salesmanId;

    private String salesmanName;

    // 贷款额度档次:1-13W以下/2-13至20W/3-20W以上
    private Integer amountGrade;

    private Long areaId;

    private String prov;

    private String city;

    private String feature;

    private Byte carType;

    private String bank;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    public String getProcessInstId() {
        return processInstId;
    }

    public void setProcessInstId(String processInstId) {
        this.processInstId = processInstId;
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
        this.salesmanName = salesmanName;
    }

    public Integer getAmountGrade() {
        return amountGrade;
    }

    public void setAmountGrade(Integer amountGrade) {
        this.amountGrade = amountGrade;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Byte getCarType() {
        return carType;
    }

    public void setCarType(Byte carType) {
        this.carType = carType;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

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

    public Integer getTotalPayAmount() {
        return totalPayAmount;
    }

    public void setTotalPayAmount(Integer totalPayAmount) {
        this.totalPayAmount = totalPayAmount;
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

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getInsuId() {
        return insuId;
    }

    public void setInsuId(Long insuId) {
        this.insuId = insuId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
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