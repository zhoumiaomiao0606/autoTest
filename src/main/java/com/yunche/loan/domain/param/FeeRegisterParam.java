package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class FeeRegisterParam {
    private Long id;

    private String undertakeMoney;

    private String undertakeFine;

    private String undertakeFee;

    private String undertakeInterest;

    private String undertakeTotal;

    private Long orderId;
}