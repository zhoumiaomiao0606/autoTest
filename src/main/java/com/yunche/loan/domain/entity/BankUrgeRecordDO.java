package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class BankUrgeRecordDO {
    private Long orderId;

    private String operator;

    private String sendee;

    private Byte urgeStatus;

    private Long bankRepayImpRecordId;

   }