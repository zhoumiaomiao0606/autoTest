package com.yunche.loan.domain.vo;

import java.util.Date;

public class FinancialProductVO {
    private Long prodId;

    private String prodName;

    private String bankName;

    private String mnemonicCode;

    private String account;

    private String signPhone;

    private String signBankCode;

    private Integer bizType;

    private String categorySuperior;

    private String categoryJunior;

    private String rate;

    private Integer mortgageTerm;

    private Long areaId;

    private String prov;

    private Long provId;

    private String city;

    private Long cityId;

    private String feature;

    private Integer status;

    public Long getProvId() {
        return provId;
    }

    public void setProvId(Long provId) {
        this.provId = provId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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

    private Date gmtCreate;

    private Date gmtModify;

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getMnemonicCode() {
        return mnemonicCode;
    }

    public void setMnemonicCode(String mnemonicCode) {
        this.mnemonicCode = mnemonicCode == null ? null : mnemonicCode.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getSignPhone() {
        return signPhone;
    }

    public void setSignPhone(String signPhone) {
        this.signPhone = signPhone == null ? null : signPhone.trim();
    }

    public String getSignBankCode() {
        return signBankCode;
    }

    public void setSignBankCode(String signBankCode) {
        this.signBankCode = signBankCode == null ? null : signBankCode.trim();
    }

    public String getCategorySuperior() {
        return categorySuperior;
    }

    public void setCategorySuperior(String categorySuperior) {
        this.categorySuperior = categorySuperior == null ? null : categorySuperior.trim();
    }

    public String getCategoryJunior() {
        return categoryJunior;
    }

    public void setCategoryJunior(String categoryJunior) {
        this.categoryJunior = categoryJunior == null ? null : categoryJunior.trim();
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate == null ? null : rate.trim();
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Integer getMortgageTerm() {
        return mortgageTerm;
    }

    public void setMortgageTerm(Integer mortgageTerm) {
        this.mortgageTerm = mortgageTerm;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }
}