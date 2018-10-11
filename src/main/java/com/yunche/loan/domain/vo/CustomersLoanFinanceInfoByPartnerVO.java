package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomersLoanFinanceInfoByPartnerVO
{
    //客户姓名
    private String customerName;

    //身份证号
    private String customerCardId;

    //手机号
    private String customerPhone;

    //银行分期本金
    private BigDecimal financialBankPeriodPrincipal;


}
