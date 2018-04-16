package com.yunche.loan.domain.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class FlowOperationMsgDO {
    private Long id;

    private Long employeeId;

    private Long orderId;

    private String title;

    private String prompt;

    private String msg;

    private String sender;

    private String processKey;

    private Date sendDate;

    private Byte readStatus;

    private Byte type;

    private String feature;

    private Byte status;
}