package com.yunche.loan.domain.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class FlowOperationMsgDO {
    private Long id;

    @NotNull
    private Long employeeId;

    @NotNull
    private Long orderId;

    @NotNull
    private String title;

    @NotNull
    private String prompt;

    @NotNull
    private String msg;

    @NotNull
    private String sender;

    @NotNull
    private String processKey;

    @NotNull
    private Date sendDate;

    @NotNull
    private Byte readStatus;

    @NotNull
    private Byte type;

    private String feature;

    @NotNull
    private Byte status;

}