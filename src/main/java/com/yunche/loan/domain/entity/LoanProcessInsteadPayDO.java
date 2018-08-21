package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessInsteadPayDO {
    private Long id;

    private Long orderId;

    private String processInstId;

    private Byte applyInsteadPay;

    private Byte financeInsteadPayReview;

    private Byte partnerInsteadPay;

    private Byte partnerInsteadPayReview;

    private Date gmtCreate;

    private Date gmtModify;
}