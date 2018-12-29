package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class RemitSatusParam
{
    private Byte remitSatus;

    private Long orderId;

    private Long serialNo;
}
