package com.yunche.loan.domain.viewObj;

import com.yunche.loan.domain.dataObj.CustBaseInfoDO;

import java.io.Serializable;
import java.util.Date;

public class InstLoanOrderVO implements Serializable {
    private Long orderId;

    private Long custId;

    private Long prodId;

    private Integer loanAmount;

    private Integer firstPayAmount;

    private Integer totalPayAmount;

    private Integer firstMonthPayAmount;

    private Integer eachMonthPayAmount;

    private Long carId;

    private Long insuId;

    private String prov;

    private String city;

    private Integer status;

    private String feature;

    private Date gmtCreate;

    private Date gmtModify;

    private CustBaseInfoVO custBaseInfoVO;

    public CustBaseInfoVO getCustBaseInfoVO() {
        return custBaseInfoVO;
    }

    public void setCustBaseInfoVO(CustBaseInfoVO custBaseInfoVO) {
        this.custBaseInfoVO = custBaseInfoVO;
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