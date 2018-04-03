package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessLogDO {
    private Long id;

    private Long orderId;

    private String taskDefinitionKey;

    private Byte action;

    private Long userId;

    private String userName;

    private String info;

    private Date createTime;
}