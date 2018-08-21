package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessCollectionDO implements LoanProcessDO_ {

    private Long id;

    private Long orderId;

    private Long collectionOrderId;

    private String processInstId;

    private Byte collectionWorkbench;

    private Byte visitCollectionReview;

    private Byte visitCollection;

    private Byte carHandle;

    private Byte carOut;

    private Byte settleOrder;

    private Byte legalReview;

    private Byte legalRecord;

    private Date gmtCreate;

    private Date gmtModify;
}