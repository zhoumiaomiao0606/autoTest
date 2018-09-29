package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class FinanceRule
{
    private String car;

    private String charge;

    private String chargeType;

    private String createTime;

    private String id;

    private String interest;

    private String maximum;

    private String minimum;

    //内扣方式：0-不内扣，1-返利内扣，2-打款内扣
    private String mode;

    private String partner;

    private String ranges;

    //1-手续费  4-公正评估费5-业务保证金（风险金）6-履约保证金7-GPS（只有直营收GPS费用）8-公正抵押费9-手续费率10-业务保证金11-上牌押金12-上门家访费13-上省外牌押金
    private String type;

    private String updateTime;

    //金额
    private String value;

    private String version;

    private String year;
}
