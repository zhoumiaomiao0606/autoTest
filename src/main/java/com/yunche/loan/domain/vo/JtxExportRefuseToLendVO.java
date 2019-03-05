package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class JtxExportRefuseToLendVO
{
    private String customer;

    private String idCard;

    private String carName;

    private String carPrice;

    private String downPaymentMoney;

    private String confThirdLoanMoney;


    private String loanTime;

    private String bank;

    private String refuseToLendMan;
}
