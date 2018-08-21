package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class FileInfoParam {
    private Long id;

    private String undertakeMoney;

    private String undertakeFine;

    private String undertakeFee;

    private String undertakeInterest;

    private String undertakeTotal;

    private Long orderId;

    private Long bankRepayImpRecordId;
}