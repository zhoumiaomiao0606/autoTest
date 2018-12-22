package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PartnerDO {
    private Long id;

    private String name;

    private Long departmentId;

    private Long leaderId;

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

    private Byte payMonth;

    private BigDecimal riskBearRate;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
    /**
     * 禁用银行列表 (征信申请)
     */
    private String disableBankList;

    private String gpsCompany;

    private String gpsAccount;

    private String partnerCode;

    private Long partnerGroup;
}