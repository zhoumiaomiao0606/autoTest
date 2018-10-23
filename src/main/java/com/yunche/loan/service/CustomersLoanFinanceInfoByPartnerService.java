package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;

public interface CustomersLoanFinanceInfoByPartnerService
{
    ResultBean selectCustomersLoanFinanceInfoByPartner(CustomersLoanFinanceInfoByPartnerParam customersLoanFinanceInfoByPartnerParam);

    ResultBean selectTotalLoanFinanceInfoByPartner(Long partnerId);

    ResultBean getOrderByCustomerId(Long customerId);

    ResultBean getCustomerInfoByCustomerName(CustomerInfoByCustomerNameParam customerInfoByCustomerNameParam);

    ResultBean selectRefundOrderInfoByPartner(RefundOrderInfoByPartnerParam refundOrderInfoByPartnerParam);

    ResultBean compensationDetail(CustomersLoanFinanceInfoByPartnerParam param);

    ResultBean rebateDetailsList(FSysRebateParam param);
    ResultBean rebateDetails(FSysRebateParam param);
    ResultBean rebateEnterAccount(FinancialRebateEnterAccountparam param);
    ResultBean rebateDetailsefresh(FSysRebateParam param);
}
