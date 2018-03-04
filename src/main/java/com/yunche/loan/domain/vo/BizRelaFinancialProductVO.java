package com.yunche.loan.domain.vo;

public class BizRelaFinancialProductVO {
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

    private String city;

    private String feature;

    private Integer status;

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getMnemonicCode() {
        return mnemonicCode;
    }

    public void setMnemonicCode(String mnemonicCode) {
        this.mnemonicCode = mnemonicCode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSignPhone() {
        return signPhone;
    }

    public void setSignPhone(String signPhone) {
        this.signPhone = signPhone;
    }

    public String getSignBankCode() {
        return signBankCode;
    }

    public void setSignBankCode(String signBankCode) {
        this.signBankCode = signBankCode;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public String getCategorySuperior() {
        return categorySuperior;
    }

    public void setCategorySuperior(String categorySuperior) {
        this.categorySuperior = categorySuperior;
    }

    public String getCategoryJunior() {
        return categoryJunior;
    }

    public void setCategoryJunior(String categoryJunior) {
        this.categoryJunior = categoryJunior;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
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
}