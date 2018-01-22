package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InsuranceCompanyDO {
    private Long id;

    private String name;

    private String mnemonicCode;

    private String contact;

    private String tel;

    private String officePhone;

    private String fax;

    private String address;

    private BigDecimal businessInsuranceRebate;

    private BigDecimal trafficInsuranceRebate;

    private String bank;

    private String accountName;

    private String bankAccount;

    private String file;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private String cooperationPolicy;
}