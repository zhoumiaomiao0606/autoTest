package com.yunche.loan.domain.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UniversalBankInterfaceSerialVO {
    private String serialNo;

    private String customerId;

    private String status;

    private String requestTime;

    private String callbackTime;
}
