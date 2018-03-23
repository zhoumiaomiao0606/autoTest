package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanCustomerDO {
    private Long id;

    private String name;

    private String nation;

    private Date birth;

    private Date identityValidity;

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
}