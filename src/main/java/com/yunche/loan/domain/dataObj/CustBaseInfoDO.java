package com.yunche.loan.domain.dataObj;

import java.io.Serializable;
import java.util.Date;

public class CustBaseInfoDO implements Serializable {
    private Long custId;

    private String custName;

    private String identityType;

    private String identityNumber;

    private String phone;

    private String marry;

    private Date birth;

    private Integer age;

    private String education;

    private String address;

    private String identityAddress;

    private Date identityValidity;

    private String companyName;

    private String companyPhone;

    private Integer bankCreditStatus;

    private String bankCreditDetail;

    private Integer socialCreditStatus;

    private String socialCreditDetail;

    private String income;

    private String houseType;

    private String houseOwner;

    private String houseFeature;

    private String houseAddress;

    private String feature;

    private String contactName;

    private String contactPhone;

    private String contactRelation;

    private Date gmtCreate;

    private Date gmtModify;

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName == null ? null : custName.trim();
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType == null ? null : identityType.trim();
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber == null ? null : identityNumber.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getMarry() {
        return marry;
    }

    public void setMarry(String marry) {
        this.marry = marry == null ? null : marry.trim();
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }


    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education == null ? null : education.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getIdentityAddress() {
        return identityAddress;
    }

    public void setIdentityAddress(String identityAddress) {
        this.identityAddress = identityAddress == null ? null : identityAddress.trim();
    }

    public Date getIdentityValidity() {
        return identityValidity;
    }

    public void setIdentityValidity(Date identityValidity) {
        this.identityValidity = identityValidity;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName == null ? null : companyName.trim();
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone == null ? null : companyPhone.trim();
    }


    public String getBankCreditDetail() {
        return bankCreditDetail;
    }

    public void setBankCreditDetail(String bankCreditDetail) {
        this.bankCreditDetail = bankCreditDetail == null ? null : bankCreditDetail.trim();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getBankCreditStatus() {
        return bankCreditStatus;
    }

    public void setBankCreditStatus(Integer bankCreditStatus) {
        this.bankCreditStatus = bankCreditStatus;
    }

    public Integer getSocialCreditStatus() {
        return socialCreditStatus;
    }

    public void setSocialCreditStatus(Integer socialCreditStatus) {
        this.socialCreditStatus = socialCreditStatus;
    }

    public String getSocialCreditDetail() {
        return socialCreditDetail;
    }

    public void setSocialCreditDetail(String socialCreditDetail) {
        this.socialCreditDetail = socialCreditDetail == null ? null : socialCreditDetail.trim();
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income == null ? null : income.trim();
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType == null ? null : houseType.trim();
    }

    public String getHouseOwner() {
        return houseOwner;
    }

    public void setHouseOwner(String houseOwner) {
        this.houseOwner = houseOwner == null ? null : houseOwner.trim();
    }

    public String getHouseFeature() {
        return houseFeature;
    }

    public void setHouseFeature(String houseFeature) {
        this.houseFeature = houseFeature == null ? null : houseFeature.trim();
    }

    public String getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress == null ? null : houseAddress.trim();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName == null ? null : contactName.trim();
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone == null ? null : contactPhone.trim();
    }

    public String getContactRelation() {
        return contactRelation;
    }

    public void setContactRelation(String contactRelation) {
        this.contactRelation = contactRelation == null ? null : contactRelation.trim();
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