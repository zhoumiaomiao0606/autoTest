package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessInsteadPayDO implements LoanProcessDO_ {

    private Long id;

    private Long orderId;
    /**
     * 批次号
     */
    private Long bankRepayImpRecordId;

    private String processInstId;

    private Byte applyInsteadPay;

    private Byte financeInsteadPayReview;

    private Byte partnerInsteadPay;

    private Byte partnerInsteadPayReview;

    private Date gmtCreate;

    private Date gmtModify;
}