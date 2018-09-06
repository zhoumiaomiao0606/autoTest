package com.yunche.loan.web.controller.chart;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.SocialCreditChartParam;
import com.yunche.loan.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-06 17:03
 * @description: 社会征信报表
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/app/loanorder/chart")
public class SocialCreditChartController
{
    @Autowired
    private ChartService chartService;
    @PostMapping(value = "/socialCreditChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean socialCreditChart(@RequestBody @Validated SocialCreditChartParam param)
    {


      return chartService.getSocialCreditChart(param);
    }
}
