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

    /**
     * 当前记录，是否已经自动打回过：0-否;1-是;
     */
    private Byte autoReject;
}