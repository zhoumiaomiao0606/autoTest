package com.yunche.loan.domain.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BankInterfaceSerialDO {
    private String serialNo;

    private Long customerId;

    private Byte status;

    private Timestamp requestTime;

    private Timestamp callbackTime;

    private String rejectReason;

    private String transCode;

    private Byte apiStatus;
}