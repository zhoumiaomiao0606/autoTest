package com.yunche.loan.domain.viewObj;

import com.yunche.loan.domain.dataObj.CustRelaPersonInfoDO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CustBaseInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long custId;

    private String custName;

    private String identityType;

    private String identityNumber;

    private String phone;

    private String marry;

    private Date birth;

    private String sex;

    private Integer age;

    private String nation;

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

    private Date gmtCreate;

    private Date gmtModify;

    private List<CustRelaPersonInfoDO> relaPersonList;   // 共贷人/担保人/反担保人

    public List<CustRelaPersonInfoDO> getRelaPersonList() {
        return relaPersonList;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setRelaPersonList(List<CustRelaPersonInfoDO> relaPersonList) {
        this.relaPersonList = relaPersonList;
    }

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

    public Integer getBankCreditStatus() {
        return bankCreditStatus;
    }

    public void setBankCreditStatus(Integer bankCreditStatus) {
        this.bankCreditStatus = bankCreditStatus;
    }

    public String getBankCreditDetail() {
        return bankCreditDetail;
    }

    public void setBankCreditDetail(String bankCreditDetail) {
        this.bankCreditDetail = bankCreditDetail;
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
        this.socialCreditDetail = socialCreditDetail;
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