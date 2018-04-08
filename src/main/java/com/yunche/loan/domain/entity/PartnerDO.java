package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PartnerDO {
    private Long id;

    private String name;

    private Long departmentId;

    private String leaderName;

    private String leaderMobile;

    private String tel;

    private String fax;

    private Long areaId;

    private Byte bizType;

    private Byte sign;

    private String cooperationScale;

    private BigDecimal execRate;

    private String cooperationInsuranceCompany;

    private String openBank;

    private String accountName;

    private String bankAccount;

    private String openBankTwo;

    private String accountNameTwo;

    private String bankAccountTwo;

    private Byte payMonth;
    /**
     * 风险承担比例
     */
    private BigDecimal riskBearRate;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}