package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.TelephoneVerifyChartByOperatorChartParam;

public interface TelephoneVerifyChartService
{
    ResultBean getTelephoneVerifyChartByOperatorChart(TelephoneVerifyChartByOperatorChartParam param);
}
