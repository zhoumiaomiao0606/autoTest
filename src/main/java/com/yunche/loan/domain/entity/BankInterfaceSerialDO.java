package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BankInterfaceSerialDO {

    private String serialNo;

    private Long orderId;

    private Long customerId;

    private Byte status;

    private Date requestTime;

    private Date callbackTime;

    private String rejectReason;

    private String transCode;

    private Integer apiStatus;

    private String apiMsg;

    private Integer fileNum;

}