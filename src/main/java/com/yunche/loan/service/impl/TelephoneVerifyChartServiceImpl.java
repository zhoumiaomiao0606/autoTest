package com.yunche.loan.service.impl;

import com.yunche.loan.service.TelephoneVerifyChartService;
import org.activiti.engine.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-06 15:45
 * @description: 电审报表服务类
 **/
@Service
public class TelephoneVerifyChartServiceImpl implements TelephoneVerifyChartService
{
    @Autowired
    private HistoryService historyService;

    public void getTelephoneVerifyChart()
    {
        /*historyService.createHistoricTaskInstanceQuery()
                .taskDefinitionKey("filter_loan_apply_visit_verify")*/
    }
}
