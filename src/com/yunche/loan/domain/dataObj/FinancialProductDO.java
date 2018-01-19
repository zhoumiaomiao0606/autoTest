package com.yunche.loan.domain.dataObj;

import java.util.Date;

public class FinancialProductDO {
    private Long prodId;

    private String bankName;

    private String mnemonicCode;

    private String account;

    private String signPhone;

    private String signBankCode;

    private Byte bizType;

    private String categorySuperior;

    private String categoryJunior;

    private String rate;

    private Byte mortgageTerm;

    private Long areaId;

    private Date gmtCreate;

    private Date gmtModify;

    private String prodName;

    private String feature;

    private Byte status;

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

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
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

    public Byte getMortgageTerm() {
        return mortgageTerm;
    }

    public void setMortgageTerm(Byte mortgageTerm) {
        this.mortgageTerm = mortgageTerm;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
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

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName == null ? null : prodName.trim();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}