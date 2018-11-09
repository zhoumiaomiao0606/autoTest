package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class EvaluateParam
{
    private String TrimId;//车型 ID

    private String Mileage;//里程

    private String BuyCarDate;//上牌时间

    private String plate_num;//车牌号
}
