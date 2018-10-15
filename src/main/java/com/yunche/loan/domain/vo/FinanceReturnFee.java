package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class FinanceReturnFee
{

    //不内扣
    private String sumRebateRealityNotRemove;

    //不内扣
    private String sumRebateReality;

    //实际打款
    private String realityPay;

    private String costType;

    //打款内扣
    private String sumPay;

    //实际返利
    private String rebateReality;

    //公司收益
   /* private String rebateCompany;*/

    //第一道返利
    private String rebateFirst;

    //第二道返利
    private String rebateSecond;

    //返利不内扣
    private String rebateRealityNotRemove;

    private List<FinanceRule> listRule;
}
