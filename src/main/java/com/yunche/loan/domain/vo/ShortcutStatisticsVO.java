package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShortcutStatisticsVO
{
    private BigDecimal totalLoanAcount =new BigDecimal(0);

    private BigDecimal totalRemitAmount =new BigDecimal(0);

    private BigDecimal totalBankPeriodPrincipal =new BigDecimal(0);
}
