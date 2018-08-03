package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanBankCardSendDO {

    private Long orderId;

    private String cardholderName;

    private String cardholderPhone;

    private String cardholderAddress;

    private String repayCardNum;

    private String expressSendAddress;

    private Byte expressCom;

    private String expressSendNum;

    private Date expressSendDate;

    private Date gmtCreate;

    private Date gmtModify;
}