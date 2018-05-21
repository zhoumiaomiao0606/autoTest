package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TaskDistributionDO {
    private Long id;

    private Long taskId;

    private Long sendee;

    private String sendeeName;

    private Date getCreate;

    private Date finishCreate;

    private Date releaseCreate;

    private Byte status;

    private String taskKey;
}