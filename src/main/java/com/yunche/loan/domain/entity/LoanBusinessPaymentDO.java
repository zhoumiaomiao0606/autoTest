package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanBusinessPaymentDO {
    private Long orderId;

    private Date applicationDate;

    private String receiveOpenBank;

    private String receiveAccount;

    private String accountNumber;

    private String paymentOrganization;

    private Byte isSendback;

    private String remark;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;


}