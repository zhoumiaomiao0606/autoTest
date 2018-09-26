package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class RuleDetailPara
{
    private Integer id;
    private Integer value;
    //内扣方式：0-不内扣，1-返利内扣，2-打款内扣
    private String type;
}
