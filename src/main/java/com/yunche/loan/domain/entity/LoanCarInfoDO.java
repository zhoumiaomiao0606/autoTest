package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanCarInfoDO {
    private Long id;

    private Long carModelId;

    private Byte carType;

    private Long partnerId;

    private Integer gpsNum;

    private Byte carKey;

    private String openBank;

    private String accountName;

    private String bankAccount;

    private Byte payMonth;

    private String info;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;
}