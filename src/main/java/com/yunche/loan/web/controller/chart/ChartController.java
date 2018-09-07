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

    /** 
    * @Author: ZhongMingxiao 
    * @Param:  
    * @return:  
    * @Date:  
    * @Description: 银行驻点部 - 社会征信查询明细
    */ 
    @PostMapping(value = "/socialCreditChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean socialCreditChart(@RequestBody @Validated SocialCreditChartParam param)
    {


      return chartService.getSocialCreditChart(param);
    }

    /** 
    * @Author: ZhongMingxiao 
    * @Param:  
    * @return:  
    * @Date:  
    * @Description: 银行驻点部 - 银行征信查询明细
    */ 
    @PostMapping(value = "/bankCreditChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean bankCreditChart(@RequestBody @Validated BankCreditChartParam param)
    {


        return chartService.getBankCreditChart(param);
    }

    
    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description: 财务部 - 财务垫款明细表
    */ 
    @PostMapping(value = "/financialDepartmentRemitDetailChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean financialDepartmentRemitDetailChart(@RequestBody @Validated FinancialDepartmentRemitDetailChartParam param)
    {

        return chartService.getFinancialDepartmentRemitDetailChart(param);
    }


    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  抵押部 - 抵押超期天数
    */
    @PostMapping(value = "/mortgageOverdueChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean mortgageOverdueChart(@RequestBody @Validated MortgageOverdueParam param)
    {

        return chartService.getMortgageOverdueChart(param);
    }


    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description: 渠道部 - 资料审核明细
    */
    @PostMapping(value = "/materialReviewChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean materialReviewChart(@RequestBody @Validated MaterialReviewParam param)
    {

        return chartService.getMaterialReviewChart(param);
    }

    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description:  渠道部 - 待垫款客户业务审批明细
    */
    @PostMapping(value = "/awaitRemitDetailChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean awaitRemitDetailChart(@RequestBody @Validated AwaitRemitDetailChartParam param)
    {

        return chartService.getAwaitRemitDetailChart(param);
    }

    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description:   渠道部 - 公司垫款订单明细表
    */ 

}
