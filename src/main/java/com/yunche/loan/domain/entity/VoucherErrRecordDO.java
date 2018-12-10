package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class VoucherErrRecordDO {
    private String serialNo;

    private Long orderId;

    private Long processId;

    private String taskDefinitionKey;

    private String retStatus;

    private String retMessage;

    private Date createTime;

    private Byte status;


}