package com.yunche.loan.service;

import com.yunche.loan.domain.param.*;

public interface ExportQueryService
{
    String exportBankCreditQuery(ExportBankCreditQueryVerifyParam exportBankCreditQueryVerifyParam);

    public String exportBankCreditQueryForChart(BankCreditChartParam param);


    String expertSocialCreditQuery(ExportSocialCreditQueryVerifyParam exportSocialCreditQueryVerifyParam);

    public String expertSocialCreditQueryForChart(SocialCreditChartParam param);


    String expertRemitDetailQuery(ExportRemitDetailQueryVerifyParam exportRemitDetailQueryVerifyParam);

    public String expertRemitDetailQueryForChart(FinancialDepartmentRemitDetailChartParam param);


    String expertMaterialReviewQuery(ExportMaterialReviewQueryVerifyParam exportMaterialReviewQueryVerifyParam);

    public String expertMaterialReviewQueryForChart(MaterialReviewParam param);


    String expertMortgageOverdueQuery(ExportMortgageOverdueQueryVerifyParam exportMortgageOverdueQueryVerifyParam);

    public String expertMortgageOverdueQueryForChart(MortgageOverdueParam param);


    String exportOrders(ExportOrdersParam exportOrdersParam);

    String expertRemitDetailQueryForRemitOrder(ExportRemitDetailQueryVerifyParam exportRemitDetailQueryVerifyParam);

    String exportCustomerInfo(ExportCustomerInfoParam exportCustomerInfoParam);


    public String expertAwaitRemitDetailChart(AwaitRemitDetailChartParam param);

    public String expertCompanyRemitDetailChart(CompanyRemitDetailChartParam param);

}
