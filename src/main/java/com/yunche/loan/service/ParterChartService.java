package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CreditApplyCustomerByMonthChartParam;
import com.yunche.loan.domain.param.LoanApplyOrdersByMonthChartParam;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-10 11:13
 * @description:
 **/
public interface ParterChartService
{
    ResultBean getCreditApplyCustomerByMonthChart(CreditApplyCustomerByMonthChartParam param);

    ResultBean getLoanApplyOrdersByMonthChart(LoanApplyOrdersByMonthChartParam param);
}
