package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanCarInfoDO {

    private Long id;

    private Long carDetailId;

    private String carDetailName;

    private Byte carType;

    private Integer gpsNum;

    private Byte carKey;

    private Long partnerId;

    private String partnerName;

    private String openBank;

    private String accountName;

    private String bankAccount;

    private Byte payMonth;

    private String info;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private Date firstRegisterDate;

    private Byte businessSource;

    private Byte cooperationDealer;

    private Byte vehicleProperty;

    private Byte  carCategory;
}