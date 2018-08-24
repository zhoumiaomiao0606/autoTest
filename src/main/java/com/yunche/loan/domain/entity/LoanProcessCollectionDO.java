package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessCollectionDO implements LoanProcessDO_ {

    private Long id;

    private Long orderId;
    /**
     * 催收批次号
     */
    private Long bankRepayImpRecordId;

    private String processInstId;

    private Byte collectionWorkbench;

    private Byte visitCollectionReview;

    private Byte visitCollection;

    private Byte carHandle;

    private Byte carOut;

    private Byte settleOrder;

    private Byte orderStatus;

    private String cancelTaskDefKey;

    private Date gmtCreate;

    private Date gmtModify;
}