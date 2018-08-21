package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TaskDistributionDO
{
    private Long taskId;

    private String taskKey;

    private Long sendee;

    private String sendeeName;

    private Date getCreate;

    private Date finishCreate;

    private Byte status;
}