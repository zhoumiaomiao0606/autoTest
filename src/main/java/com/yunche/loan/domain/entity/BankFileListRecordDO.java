package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BankFileListRecordDO {
    private Long bankFileListId;

    private Long customerId;

    private Long orderId;

    private String areaId;

    private String platNo;

    private String guarantyUnit;

    private Date opencardDate;

    private Integer cardNumber;

    private String name;

    private String cardType;

    private String credentialNo;

    private String hairpinFlag;

    private String accountStatement;

    private String repayDate;

    private Date gmtGreate;

    private Byte status;
}