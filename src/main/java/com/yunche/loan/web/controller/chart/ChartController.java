package com.yunche.loan.web.controller.chart;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.service.ChartService;
import com.yunche.loan.service.ExportQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-06 17:03
 * @description: 报表
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/chart")
public class ChartController
{
    @Autowired
    private ChartService chartService;

    @Resource
    private ExportQueryService exportQueryService;

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
     * EXCEl银行驻点部 - 社会征信查询明细
     */
    @PostMapping(value = "/exportSocialCreditQuery")
    public ResultBean expertSocialCreditQuery(@RequestBody @Validated SocialCreditChartParam param)
    {
        return ResultBean.ofSuccess(chartService.expertSocialCreditQueryForChart(param)
        );
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
     * EXCEl银行驻点部 - 银行征信查询明细
     */
    @PostMapping(value = "/exportBankCreditQuery")
    public ResultBean exportBankCreditQuery(@RequestBody BankCreditChartParam param)
    {
        return ResultBean.ofSuccess(exportQueryService.exportBankCreditQueryForChart(param));
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
     * EXCEl财务部 - 财务垫款明细表
     */
    @PostMapping(value = "/expertRemitDetailQueryForChart")
    public ResultBean exportfinancialDepartmentRemitDetailChart(@RequestBody FinancialDepartmentRemitDetailChartParam param)
    {
        return ResultBean.ofSuccess(exportQueryService.expertRemitDetailQueryForChart(param));
    }

    /**
     * EXCEl财务部 - 快捷统计
     */
    @PostMapping(value = "/financialDepartmentRemitDetailChartShortcutStatistics")
    public ResultBean financialDepartmentRemitDetailChartShortcutStatistics(@RequestBody FinancialDepartmentRemitDetailChartParam param)
    {
        return chartService.financialDepartmentRemitDetailChartShortcutStatistics(param);
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
     * EXCEl抵押部 - 抵押超期天数
     */
    @PostMapping(value = "/expertMortgageOverdueQueryForChart")
    public ResultBean expertMortgageOverdueQueryForChart(@RequestBody MortgageOverdueParam param)
    {
        return ResultBean.ofSuccess(exportQueryService.expertMortgageOverdueQueryForChart(param));
    }

    /**
     * EXCEl抵押部 - 快捷统计
     */
    @PostMapping(value = "/mortgageOverdueQueryForChartShortcutStatistics")
    public ResultBean mortgageOverdueQueryForChartShortcutStatistics(@RequestBody MortgageOverdueParam param)
    {
        return chartService.mortgageOverdueQueryForChartShortcutStatistics(param);
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
     * EXCEl渠道部 - 资料审核明细
     */
    @PostMapping(value = "/expertMaterialReviewQueryForChart")
    public ResultBean expertMaterialReviewQueryForChart(@RequestBody MaterialReviewParam param)
    {
        return ResultBean.ofSuccess(exportQueryService.expertMaterialReviewQueryForChart(param));
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
     * EXCEl渠道部 - 待垫款客户业务审批明细
     */
    @PostMapping(value = "/expertAwaitRemitDetailChart")
    public ResultBean expertAwaitRemitDetailChart(@RequestBody AwaitRemitDetailChartParam param)
    {
        return ResultBean.ofSuccess(exportQueryService.expertAwaitRemitDetailChart(param));
    }

    @GetMapping(value = "/usertaskMaterialPrintUsers")
    public ResultBean usertaskMaterialPrintUsers()
    {
        return ResultBean.ofSuccess(exportQueryService.usertaskMaterialPrintUsers());
    }

    /**
     * EXCEl 渠道部 - 快捷统计
     */
    @PostMapping(value = "/awaitRemitDetailChartShortcutStatistics")
    public ResultBean awaitRemitDetailChartShortcutStatistics(@RequestBody AwaitRemitDetailChartParam param)
    {
        return chartService.awaitRemitDetailChartShortcutStatistics(param);
    }
    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description:   渠道部 - 公司垫款订单明细表
    */
    @PostMapping(value = "/companyRemitDetailChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean companyRemitDetailChart(@RequestBody @Validated CompanyRemitDetailChartParam param)
    {
        return chartService.getCompanyRemitDetailChart(param);
    }

    /**
     * EXCEl 渠道部 - 快捷统计
     */
    @PostMapping(value = "/companyRemitDetailChartShortcutStatistics")
    public ResultBean companyRemitDetailChartShortcutStatistics(@RequestBody CompanyRemitDetailChartParam param)
    {
        return chartService.companyRemitDetailChartShortcutStatistics(param);
    }

    /**
     * EXCEl渠道部 - 公司垫款订单明细表
     */
    @PostMapping(value = "/expertCompanyRemitDetailChart")
    public ResultBean expertCompanyRemitDetailChart(@RequestBody CompanyRemitDetailChartParam param)
    {
        return ResultBean.ofSuccess(exportQueryService.expertCompanyRemitDetailChart(param));
    }

    /*
    城站银行未抵押明细表
     */
    @PostMapping(value = "/hzBankNotMortgage", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean hzBankNotMortgage(@RequestBody MaterialReviewParam param)
    {
        return chartService.hzBankNotMortgage(param);
    }

    /**
     * 渠道部时效表
     */
    @PostMapping(value = "/channelPrescription", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean channelPrescription(@RequestBody MaterialReviewParam param)
    {
        return chartService.channelPrescription(param);
    }


    /**
     * 业务服务部流转表
     */
    @PostMapping(value = "/busDataFlow", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean busDataFlow(@RequestBody MaterialReviewParam param)
    {
        return chartService.busDataFlow(param);
    }

    /*
    征信时效表
     */
    @PostMapping(value = "/creditPrescription", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditPrescription(@RequestBody MaterialReviewParam param)
    {
        return chartService.creditPrescription(param);
    }

    /**
     * 纸审问题预警表
     */
    @PostMapping(value = "/examineEarlyWarning", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean examineEarlyWarning(@RequestBody MaterialReviewParam param)
    {
        return chartService.examineEarlyWarning(param);
    }
}
