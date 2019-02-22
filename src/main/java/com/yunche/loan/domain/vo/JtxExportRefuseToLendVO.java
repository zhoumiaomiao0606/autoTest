package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class JtxExportRefuseToLendVO
{
    private String name;

    private String idCard;

    private String carName;

    private String carPrice;

    private String downPaymentMoney;

    private String lendAmount;


    private String loanTime;

    private String bank;

    private String refuseToLendMan;
}
