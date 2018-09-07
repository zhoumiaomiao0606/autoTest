package com.yunche.loan.web.controller.chart;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-06 17:03
 * @description: 报表
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/app/loanorder/chart")
public class ChartController
{
    @Autowired
    private ChartService chartService;

    @PostMapping(value = "/socialCreditChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean socialCreditChart(@RequestBody @Validated SocialCreditChartParam param)
    {


      return chartService.getSocialCreditChart(param);
    }

    @PostMapping(value = "/bankCreditChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean bankCreditChart(@RequestBody @Validated BankCreditChartParam param)
    {


        return chartService.getBankCreditChart(param);
    }

    @PostMapping(value = "/financialDepartmentRemitDetailChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean financialDepartmentRemitDetailChart(@RequestBody @Validated FinancialDepartmentRemitDetailChartParam param)
    {

        return chartService.getFinancialDepartmentRemitDetailChart(param);
    }

    @PostMapping(value = "/mortgageOverdueChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean mortgageOverdueChart(@RequestBody @Validated MortgageOverdueParam param)
    {

        return chartService.getMortgageOverdueChart(param);
    }
}
