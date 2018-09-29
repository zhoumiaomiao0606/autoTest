package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CustomersLoanFinanceInfoByPartnerParam;
import com.yunche.loan.domain.vo.CustomersLoanFinanceInfoByPartnerVO;

public interface CustomersLoanFinanceInfoByPartnerService
{
    ResultBean selectCustomersLoanFinanceInfoByPartner(CustomersLoanFinanceInfoByPartnerParam customersLoanFinanceInfoByPartnerParam);

    ResultBean selectTotalLoanFinanceInfoByPartner(Long partnerId);
}
