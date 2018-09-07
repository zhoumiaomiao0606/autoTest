package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;

public interface ChartService
{
    ResultBean getSocialCreditChart(SocialCreditChartParam param);

    ResultBean getBankCreditChart(BankCreditChartParam param);

    ResultBean getFinancialDepartmentRemitDetailChart(FinancialDepartmentRemitDetailChartParam param);

    ResultBean getMortgageOverdueChart(MortgageOverdueParam param);

    ResultBean getMaterialReviewChart(MaterialReviewParam param);
}
