package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class LoanBalanceByPartnerVO extends CustomersLoanFinanceInfoByPartnerVO
{
    private Date lendDate;
}
