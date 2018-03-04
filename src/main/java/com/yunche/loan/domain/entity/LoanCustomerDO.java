package com.yunche.loan.domain.entity;

import lombok.Data;

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

    private Integer monthIncome;

    private Byte houseType;

    private Byte houseOwner;

    private Byte houseFeature;

    private String houseAddress;

    private String info;

    private Byte bankCreditStatus;

    private String bankCreditDetail;

    private Byte socialCreditStatus;

    private String socialCreditDetail;

    private Byte custType;

    private Long principalCustId;

    private Byte custRelation;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;

    private String files;
}