package com.yunche.loan.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

public class ThirdPartyFundDO {
    private Long id;

    private String thirdPartyFundName;

    private String linkman;

    private String mobile;

    private BigDecimal basicGuaranteeMoney;

    private BigDecimal interestRateYear;

    private BigDecimal singleCost;

    private Long bankId;

    private Byte status;

    private Date gmtStarttime;

    private Date gmtStoptime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getThirdPartyFundName() {
        return thirdPartyFundName;
    }

    public void setThirdPartyFundName(String thirdPartyFundName) {
        this.thirdPartyFundName = thirdPartyFundName == null ? null : thirdPartyFundName.trim();
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman == null ? null : linkman.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public BigDecimal getBasicGuaranteeMoney() {
        return basicGuaranteeMoney;
    }

    public void setBasicGuaranteeMoney(BigDecimal basicGuaranteeMoney) {
        this.basicGuaranteeMoney = basicGuaranteeMoney;
    }

    public BigDecimal getInterestRateYear() {
        return interestRateYear;
    }

    public void setInterestRateYear(BigDecimal interestRateYear) {
        this.interestRateYear = interestRateYear;
    }

    public BigDecimal getSingleCost() {
        return singleCost;
    }

    public void setSingleCost(BigDecimal singleCost) {
        this.singleCost = singleCost;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getGmtStarttime() {
        return gmtStarttime;
    }

    public void setGmtStarttime(Date gmtStarttime) {
        this.gmtStarttime = gmtStarttime;
    }

    public Date getGmtStoptime() {
        return gmtStoptime;
    }

    public void setGmtStoptime(Date gmtStoptime) {
        this.gmtStoptime = gmtStoptime;
    }
}