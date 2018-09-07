package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.BankCreditChartParam;
import com.yunche.loan.domain.param.FinancialDepartmentRemitDetailChartParam;
import com.yunche.loan.domain.param.SocialCreditChartParam;

public interface ChartService
{
    ResultBean getSocialCreditChart(SocialCreditChartParam param);

    ResultBean getBankCreditChart(BankCreditChartParam param);

    ResultBean getFinancialDepartmentRemitDetailChart(FinancialDepartmentRemitDetailChartParam param);
}
