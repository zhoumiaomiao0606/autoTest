package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessBridgeDO implements LoanProcessDO_ {

    private Long orderId;

    private String processInstId;

    private Byte bridgeHandle;

    private Byte bridgeRepayRecord;

    private Byte bridgeInterestRecord;

    private Byte bridgeRepayInfo;

    private Date gmtCreate;

    private Date gmtModify;
}