package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BankCardRecordDO {
    private Long id;

    private Long orderId;

    private String userName;

    private String idCard;

    private String billingDate;

    private Date firstBillingDate;

    private String repayDate;

    private Date firstRepaymentDate;

    private String repayCardId;

    private Date receiveDate;

    private String sendee;

    private Byte status;

    private String feature;

}