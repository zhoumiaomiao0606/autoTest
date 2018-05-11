package com.yunche.loan.domain.entity;

import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class BankUrgeRecordDO {
    private Long orderId;

    private String operator;

    private Long sendee;

    private Byte urgeStatus;

    private Long bankRepayImpRecordId;

    private Date sendeeDate;

    private Timestamp gmtCreate;
   }