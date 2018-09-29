package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TotalLoanFinanceInfoByPartnerVO
{
    private BigDecimal totalBadBalance;

    private BigDecimal totalOverdueBalance;


    private BigDecimal totalInGuaranteeBalance;


    private BigDecimal totalLoanBalance;

}
