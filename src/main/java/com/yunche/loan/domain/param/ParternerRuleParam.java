package com.yunche.loan.domain.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ParternerRuleParam
{
    //合伙人id
    private Integer partnerId;
    //收费是否月结(0:否;1:是)
    private Boolean payMonth;
    //车辆类型：0-新车; 1-二手车; 2-不限;
    private Integer carType;
    //贷款金额
    private Integer financialLoanAmount;
    //银行分期本金
    private Integer financialBankPeriodPrincipal;
    //贷款利率
    private BigDecimal rate;
    //年限
    private Integer year;
    //GPS数量
    private Integer carGpsNum;
    //银行ID
    private Long bankAreaId;
    //上牌地城市ID
    private Long areaId;
}
