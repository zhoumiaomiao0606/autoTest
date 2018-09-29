package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InGuaranteeBalanceByPartnerVO extends CustomersLoanFinanceInfoByPartnerVO
{
    private BigDecimal inGuaranteeBalance;
}
