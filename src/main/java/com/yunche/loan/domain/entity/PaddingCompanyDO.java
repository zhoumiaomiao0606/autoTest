package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaddingCompanyDO {
    private Long id;

    private String name;

    private String mnemonicCode;

    private String contact;

    private String tel;

    private String officePhone;

    private String fax;

    private String address;

    private BigDecimal rate;

    private String bank;

    private String accountName;

    private String bankAccount;

    private String file;

    private String cooperationPolicy;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}