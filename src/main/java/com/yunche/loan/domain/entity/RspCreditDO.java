package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class RspCreditDO {
    private Long id;

    private String platformCode;

    private String applicationResult;

    private String applicationTime;

    private String platform;

    private String applicationMoney;

}