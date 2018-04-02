package com.yunche.loan.domain.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class BankCardRecordDO {
    private Integer id;

    private Long orderId;

    private String userName;

    private String idCard;

    private Date billingDate;

    private Date firstBillingDate;

    private Date repayDate;

    private Date firstRepaymentDate;

    private String repayCardId;

    private Date receiveDate;

    private String sendee;

    private Byte status;

    private String feature;


}