package com.yunche.loan.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

public class LoanCustomerDO {
    private Long id;

    private String name;

    private String nation;

    private Date birth;

    private Date identityValidity;

    private String lendCard;

    private String idCard;

    private String mobile;

    private Byte age;

    private Byte sex;

    private Date applyDate;

    private String address;

    private Byte marry;

    private String identityAddress;

    private String mobileArea;

    private Byte education;

    private String companyName;

    private String companyPhone;

    private String companyAddress;

    private BigDecimal monthIncome;

    private Byte houseType;

    private Byte houseOwner;

    private Byte houseFeature;

    private String houseAddress;

    private String info;

    private Byte custType;

    private Long principalCustId;

    private Byte custRelation;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private String postcode;

    private Long workingYears;

    private String duty;

    private Byte householdNature;

    private String childrenNum;

    private String reserveMobile;

    private Byte companyNature;

    private Byte industryCategory;

    private String incomeCertificateCompanyName;

    private String incomeCertificateCompanyAddress;

    private String familyAddress;

    private Long familyMobile;

    private BigDecimal personMonthlyIncome;

    private BigDecimal familyMonthlyIncome;

    private Long feedingNum;

    private Long floorSpace;

    private Byte houseOwnerRelation;

    private String bankCardTransmitAddress;

    private String bankCardTransmitPostcode;

    private Byte houseCertificateType;

    private Byte bankCardTransmitAddressType;

    private String companyPostcode;

    private String elementarySchool;

    private String professional;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation == null ? null : nation.trim();
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Date getIdentityValidity() {
        return identityValidity;
    }

    public void setIdentityValidity(Date identityValidity) {
        this.identityValidity = identityValidity;
    }

    public String getLendCard() {
        return lendCard;
    }

    public void setLendCard(String lendCard) {
        this.lendCard = lendCard == null ? null : lendCard.trim();
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? null : idCard.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public Byte getAge() {
        return age;
    }

    public void setAge(Byte age) {
        this.age = age;
    }

    public Byte getSex() {
        return sex;
    }

    public void setSex(Byte sex) {
        this.sex = sex;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Byte getMarry() {
        return marry;
    }

    public void setMarry(Byte marry) {
        this.marry = marry;
    }

    public String getIdentityAddress() {
        return identityAddress;
    }

    public void setIdentityAddress(String identityAddress) {
        this.identityAddress = identityAddress == null ? null : identityAddress.trim();
    }

    public String getMobileArea() {
        return mobileArea;
    }

    public void setMobileArea(String mobileArea) {
        this.mobileArea = mobileArea == null ? null : mobileArea.trim();
    }

    public Byte getEducation() {
        return education;
    }

    public void setEducation(Byte education) {
        this.education = education;
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

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress == null ? null : companyAddress.trim();
    }

    public BigDecimal getMonthIncome() {
        return monthIncome;
    }

    public void setMonthIncome(BigDecimal monthIncome) {
        this.monthIncome = monthIncome;
    }

    public Byte getHouseType() {
        return houseType;
    }

    public void setHouseType(Byte houseType) {
        this.houseType = houseType;
    }

    public Byte getHouseOwner() {
        return houseOwner;
    }

    public void setHouseOwner(Byte houseOwner) {
        this.houseOwner = houseOwner;
    }

    public Byte getHouseFeature() {
        return houseFeature;
    }

    public void setHouseFeature(Byte houseFeature) {
        this.houseFeature = houseFeature;
    }

    public String getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress == null ? null : houseAddress.trim();
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }

    public Byte getCustType() {
        return custType;
    }

    public void setCustType(Byte custType) {
        this.custType = custType;
    }

    public Long getPrincipalCustId() {
        return principalCustId;
    }

    public void setPrincipalCustId(Long principalCustId) {
        this.principalCustId = principalCustId;
    }

    public Byte getCustRelation() {
        return custRelation;
    }

    public void setCustRelation(Byte custRelation) {
        this.custRelation = custRelation;
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

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode == null ? null : postcode.trim();
    }

    public Long getWorkingYears() {
        return workingYears;
    }

    public void setWorkingYears(Long workingYears) {
        this.workingYears = workingYears;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty == null ? null : duty.trim();
    }

    public Byte getHouseholdNature() {
        return householdNature;
    }

    public void setHouseholdNature(Byte householdNature) {
        this.householdNature = householdNature;
    }

    public String getChildrenNum() {
        return childrenNum;
    }

    public void setChildrenNum(String childrenNum) {
        this.childrenNum = childrenNum == null ? null : childrenNum.trim();
    }

    public String getReserveMobile() {
        return reserveMobile;
    }

    public void setReserveMobile(String reserveMobile) {
        this.reserveMobile = reserveMobile == null ? null : reserveMobile.trim();
    }

    public Byte getCompanyNature() {
        return companyNature;
    }

    public void setCompanyNature(Byte companyNature) {
        this.companyNature = companyNature;
    }

    public Byte getIndustryCategory() {
        return industryCategory;
    }

    public void setIndustryCategory(Byte industryCategory) {
        this.industryCategory = industryCategory;
    }

    public String getIncomeCertificateCompanyName() {
        return incomeCertificateCompanyName;
    }

    public void setIncomeCertificateCompanyName(String incomeCertificateCompanyName) {
        this.incomeCertificateCompanyName = incomeCertificateCompanyName == null ? null : incomeCertificateCompanyName.trim();
    }

    public String getIncomeCertificateCompanyAddress() {
        return incomeCertificateCompanyAddress;
    }

    public void setIncomeCertificateCompanyAddress(String incomeCertificateCompanyAddress) {
        this.incomeCertificateCompanyAddress = incomeCertificateCompanyAddress == null ? null : incomeCertificateCompanyAddress.trim();
    }

    public String getFamilyAddress() {
        return familyAddress;
    }

    public void setFamilyAddress(String familyAddress) {
        this.familyAddress = familyAddress == null ? null : familyAddress.trim();
    }

    public Long getFamilyMobile() {
        return familyMobile;
    }

    public void setFamilyMobile(Long familyMobile) {
        this.familyMobile = familyMobile;
    }

    public BigDecimal getPersonMonthlyIncome() {
        return personMonthlyIncome;
    }

    public void setPersonMonthlyIncome(BigDecimal personMonthlyIncome) {
        this.personMonthlyIncome = personMonthlyIncome;
    }

    public BigDecimal getFamilyMonthlyIncome() {
        return familyMonthlyIncome;
    }

    public void setFamilyMonthlyIncome(BigDecimal familyMonthlyIncome) {
        this.familyMonthlyIncome = familyMonthlyIncome;
    }

    public Long getFeedingNum() {
        return feedingNum;
    }

    public void setFeedingNum(Long feedingNum) {
        this.feedingNum = feedingNum;
    }

    public Long getFloorSpace() {
        return floorSpace;
    }

    public void setFloorSpace(Long floorSpace) {
        this.floorSpace = floorSpace;
    }

    public Byte getHouseOwnerRelation() {
        return houseOwnerRelation;
    }

    public void setHouseOwnerRelation(Byte houseOwnerRelation) {
        this.houseOwnerRelation = houseOwnerRelation;
    }

    public String getBankCardTransmitAddress() {
        return bankCardTransmitAddress;
    }

    public void setBankCardTransmitAddress(String bankCardTransmitAddress) {
        this.bankCardTransmitAddress = bankCardTransmitAddress == null ? null : bankCardTransmitAddress.trim();
    }

    public String getBankCardTransmitPostcode() {
        return bankCardTransmitPostcode;
    }

    public void setBankCardTransmitPostcode(String bankCardTransmitPostcode) {
        this.bankCardTransmitPostcode = bankCardTransmitPostcode == null ? null : bankCardTransmitPostcode.trim();
    }

    public Byte getHouseCertificateType() {
        return houseCertificateType;
    }

    public void setHouseCertificateType(Byte houseCertificateType) {
        this.houseCertificateType = houseCertificateType;
    }

    public Byte getBankCardTransmitAddressType() {
        return bankCardTransmitAddressType;
    }

    public void setBankCardTransmitAddressType(Byte bankCardTransmitAddressType) {
        this.bankCardTransmitAddressType = bankCardTransmitAddressType;
    }

    public String getCompanyPostcode() {
        return companyPostcode;
    }

    public void setCompanyPostcode(String companyPostcode) {
        this.companyPostcode = companyPostcode == null ? null : companyPostcode.trim();
    }

    public String getElementarySchool() {
        return elementarySchool;
    }

    public void setElementarySchool(String elementarySchool) {
        this.elementarySchool = elementarySchool == null ? null : elementarySchool.trim();
    }

    public String getProfessional() {
        return professional;
    }

    public void setProfessional(String professional) {
        this.professional = professional == null ? null : professional.trim();
    }
}