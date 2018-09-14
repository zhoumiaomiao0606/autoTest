package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessBridgeDO implements LoanProcessDO_ {

    private Long id;

    private Long orderId;

    private String processInstId;

    private Byte bridgeHandle;

    private Byte bridgeRepayRecord;

    private Byte bridgeInterestRecord;

    private Byte bridgeRepayInfo;

    private Byte orderStatus;

    private String cancelTaskDefKey;

    private Date gmtCreate;

    private Date gmtModify;
}