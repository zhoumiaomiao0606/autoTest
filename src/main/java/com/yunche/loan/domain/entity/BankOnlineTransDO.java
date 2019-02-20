package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class BankOnlineTransDO {
    private Long orderId;

    private String creditStatus;

    private String openCardStatus;

    private String commonApplyStatus;

    private String multimediaStatus;

    private Integer actionTimes;
}