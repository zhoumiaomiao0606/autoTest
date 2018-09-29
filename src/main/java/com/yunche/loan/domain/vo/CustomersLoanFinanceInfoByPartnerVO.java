package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomersLoanFinanceInfoByPartnerVO
{
    private String customerName;

    private String customerCardId;

    private String customerPhone;

    private BigDecimal financialBankPeriodPrincipal;


}
