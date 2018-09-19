package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.BankCreditPrincipalQuery;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.ContractSetQuery;
import com.yunche.loan.domain.vo.*;

import java.util.List;

public interface ReportService {
    ResultBean<List<BusinessApprovalReportVO>> businessApproval(BaseQuery query);

    BusinessApprovalReportTotalVO businessApprovalTotal(BaseQuery query);

    String businessApprovalExport(BaseQuery query);



    ResultBean<List<ContractSetReportVO>> contractSet(ContractSetQuery query);

    ContractSetReportTotalVO contractSetTotal(ContractSetQuery query);

    String contractSetExport(ContractSetQuery query);


    ResultBean<List<BankCreditPrincipalVO>> bankCreditPrincipal(BankCreditPrincipalQuery query);

    ContractSetReportTotalVO bankCreditPrincipalTotal(BankCreditPrincipalQuery query);

    String bankCreditPrincipalExport(BankCreditPrincipalQuery query);


    ResultBean<List<BankCreditPrincipalVO>> bankCreditAll(BankCreditPrincipalQuery query);

    ContractSetReportTotalVO bankCreditAllTotal(BankCreditPrincipalQuery query);

    String bankCreditAllExport(BankCreditPrincipalQuery query);


    ResultBean<List<TelBankCountVO>> telBankCount(BankCreditPrincipalQuery query);

    String telBankCountExport(BankCreditPrincipalQuery query);

    ResultBean<List<TelUserCountVO>> telUserCount(BankCreditPrincipalQuery query);

    String telUserCountExport(BankCreditPrincipalQuery query);

    ResultBean<List<TelPartnerCountVO>> telPartnerCount(BankCreditPrincipalQuery query);

    String telPartnerCountExport(BankCreditPrincipalQuery query);
}
