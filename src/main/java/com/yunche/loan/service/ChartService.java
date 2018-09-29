package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;

public interface ChartService
{
    ResultBean getSocialCreditChart(SocialCreditChartParam param);

    String expertSocialCreditQueryForChart(SocialCreditChartParam param);


    ResultBean getBankCreditChart(BankCreditChartParam param);


    ResultBean getFinancialDepartmentRemitDetailChart(FinancialDepartmentRemitDetailChartParam param);

    ResultBean getMortgageOverdueChart(MortgageOverdueParam param);

    ResultBean getMaterialReviewChart(MaterialReviewParam param);

    ResultBean getAwaitRemitDetailChart(AwaitRemitDetailChartParam param);

    ResultBean getCompanyRemitDetailChart(CompanyRemitDetailChartParam param);


    ResultBean financialDepartmentRemitDetailChartShortcutStatistics(FinancialDepartmentRemitDetailChartParam param);

    ResultBean mortgageOverdueQueryForChartShortcutStatistics(MortgageOverdueParam param);

    ResultBean awaitRemitDetailChartShortcutStatistics(AwaitRemitDetailChartParam param);

    ResultBean companyRemitDetailChartShortcutStatistics(CompanyRemitDetailChartParam param);
}
