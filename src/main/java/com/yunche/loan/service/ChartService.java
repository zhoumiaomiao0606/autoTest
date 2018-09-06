package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.SocialCreditChartParam;

public interface ChartService
{
    ResultBean getSocialCreditChart(SocialCreditChartParam param);
}
