package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.Date;

@Data
public class LoanBusinessPaymentParam {

    private Long orderId;

    private Date applicationDate;

    private String receiveOpenBank;

    private String receiveAccount;

    private String accountNumber;

    private String paymentOrganization;

    private Byte isSendback;

    private String remark;

}
