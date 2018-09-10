package com.yunche.loan.web.controller.chart;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.TelephoneVerifyChartByOperatorChartParam;
import com.yunche.loan.service.TelephoneVerifyChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-06 15:42
 * @description: 电审报表统计
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/app/loanorder/chart")
public class TelephoneVerifyChartController
{
    @Autowired
    private TelephoneVerifyChartService telephoneVerifyChartService;
    
    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description:  电审按经办人统计表--日报/月报
    */
    @PostMapping(value = "/socialCreditChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean telephoneVerifyChartByOperator(@RequestBody @Validated TelephoneVerifyChartByOperatorChartParam param)
    {


        return telephoneVerifyChartService.getTelephoneVerifyChartByOperatorChart(param);
    }
    
    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description:  电审按合伙人统计表--日报/周报/月报
    */

    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  电审按银行分类统计--日报/周报/月报
    */
}
