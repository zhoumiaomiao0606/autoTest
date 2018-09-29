package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverdueBalanceByPartnerVO extends CustomersLoanFinanceInfoByPartnerVO
{
    private BigDecimal overdueBalance;
}
