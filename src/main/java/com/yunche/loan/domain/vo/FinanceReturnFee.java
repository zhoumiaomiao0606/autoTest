package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class FinanceReturnFee
{
    private String sumRebateRealityNotRemove;

    private String realityPay;

    private String costType;

    private String rebateReality;

    private String rebateSecond;

    private List<FinanceRule> listRule;
}
