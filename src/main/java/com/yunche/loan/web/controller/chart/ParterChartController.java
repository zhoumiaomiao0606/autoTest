package com.yunche.loan.web.controller.chart;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CreditApplyCustomerByMonthChartParam;
import com.yunche.loan.domain.param.LoanApplyOrdersByMonthChartParam;
import com.yunche.loan.domain.param.LoanApplyOrdersByMonthRemitDetailChartParam;
import com.yunche.loan.domain.param.TelephoneVerifyChartByOperatorChartParam;
import com.yunche.loan.service.ParterChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-10 10:04
 * @description: 合伙人报表总计
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/app/loanorder/chart/parter")
public class ParterChartController
{
    @Autowired
    private ParterChartService parterChartService;


    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description:  当月提交了征信查询的订单客户
    */
    @PostMapping(value = "/creditApplyCustomerByMonthChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditApplyCustomerByMonthChart(@RequestBody @Validated CreditApplyCustomerByMonthChartParam param)
    {
        return parterChartService.getCreditApplyCustomerByMonthChart(param);
    }
    
    
    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description:  按月为单位，提交了贷款申请单的订单
    */
    @PostMapping(value = "/loanApplyOrdersByMonthChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean loanApplyOrdersByMonthChart(@RequestBody @Validated LoanApplyOrdersByMonthChartParam param)
    {
        return parterChartService.getLoanApplyOrdersByMonthChart(param);
    }

    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  征信未通过、风控未通过、银行未通过，征信未通过
    */
    
    
    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description:  当月提交了贷款申请的订单，修改订单信息的，以最后一次订单信息为准
    */
    @PostMapping(value = "/loanApplyOrdersByMonthChartRemitDetail", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean loanApplyOrdersByMonthRemitDetailChart(@RequestBody @Validated LoanApplyOrdersByMonthRemitDetailChartParam param)
    {
        return parterChartService.getLoanApplyOrdersByMonthRemitDetailChart(param);
    }



    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  当月提交了垫款申请的订单
    */


    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  当月垫款成功的订单（排除退款订单）
    */
}
