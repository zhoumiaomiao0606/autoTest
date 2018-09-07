package com.yunche.loan.web.controller.chart;

import com.yunche.loan.service.TelephoneVerifyChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-06 15:42
 * @description: 电审报表统计
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/app/loanorder")
public class TelephoneVerifyChartController
{
    @Autowired
    private TelephoneVerifyChartService telephoneVerifyChartService;
}
