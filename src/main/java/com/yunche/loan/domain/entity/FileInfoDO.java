package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class FileInfoDO {
    private Long id;

    private String undertakeMoney;

    private String undertakeFine;

    private String undertakeFee;

    private String undertakeInterest;

    private String undertakeTotal;

    private Long orderId;

    private Long bankRepayImpRecordId;
}