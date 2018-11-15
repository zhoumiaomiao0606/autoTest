package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class QueryVINParam
{
    private String queryVIN;

    private Long orderId;

    private Long partnerId;
}
