package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BadBalanceByPartnerVO  extends CustomersLoanFinanceInfoByPartnerVO
{
    private BigDecimal badBalance;
}
