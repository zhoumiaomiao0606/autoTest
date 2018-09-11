package com.yunche.loan.service.impl;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.TelephoneVerifyChartByOperatorChartParam;
import com.yunche.loan.mapper.LoanProcessLogDOMapper;
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

    private LoanProcessLogDOMapper loanProcessLogDOMapper;

    @Override
    public ResultBean getTelephoneVerifyChartByOperatorChart(TelephoneVerifyChartByOperatorChartParam param)
    {
        //时间内 选定经办人

        //电审结果-打回
        //电审结果资料增补
        //电审结果-通过
        //电审结果-弃单
        return null;
    }
}
