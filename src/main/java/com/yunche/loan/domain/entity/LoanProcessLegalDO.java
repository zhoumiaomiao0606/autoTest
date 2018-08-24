package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessLegalDO implements LoanProcessDO_ {

    private Long id;

    private Long orderId;
    /**
     * 催收批次号
     */
    private Long collectionOrderId;

    private String processInstId;

    private Byte legalReview;

    private Byte legalRecord;

    private Byte orderStatus;

    private String cancelTaskDefKey;

    private Date gmtCreate;

    private Date gmtModify;
}