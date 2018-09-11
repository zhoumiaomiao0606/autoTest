package com.yunche.loan.service.impl;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CreditApplyCustomerByMonthChartParam;
import com.yunche.loan.domain.param.LoanApplyOrdersByMonthChartParam;
import com.yunche.loan.mapper.ChartDOMapper;
import com.yunche.loan.mapper.ParterChartDOMapper;
import com.yunche.loan.service.ParterChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-10 11:13
 * @description: 合伙人需求报表实现
 **/
@Service
public class ParterChartServiceImpl implements ParterChartService
{
    @Autowired
    private ParterChartDOMapper parterChartDOMapper;

    @Override
    public ResultBean getCreditApplyCustomerByMonthChart(CreditApplyCustomerByMonthChartParam param)
    {

        //征信客户查询量
       long creditApplyCustomerCount =  parterChartDOMapper.selectCreditApplyCustomerCount(param);
        //征信查询订单量

        //视频面签订单数

        //上门调查订单数
     /*   parterChartDOMapper.select*/
        return null;
    }

    @Override
    public ResultBean getLoanApplyOrdersByMonthChart(LoanApplyOrdersByMonthChartParam param) {
        return null;
    }
}
