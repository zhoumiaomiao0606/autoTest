package com.yunche.loan.web.controller.ext;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CustomersLoanFinanceInfoByPartnerParam;
import com.yunche.loan.service.CustomersLoanFinanceInfoByPartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 外部-财务系统
 *
 * @author liuzhe
 * @date 2018/9/25
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/ext/financialSystem")
public class FinancialSystemExtController {

    @Autowired
    private CustomersLoanFinanceInfoByPartnerService customersLoanFinanceInfoByPartnerservice;

    @GetMapping("/list")
    public ResultBean list() {

        return ResultBean.ofSuccess(null);
    }

    //获取财务信息
    @PostMapping("/customersLoanFinanceInfoByPartner")
    public ResultBean selectCustomersLoanFinanceInfoByPartner(@RequestBody CustomersLoanFinanceInfoByPartnerParam customersLoanFinanceInfoByPartnerParam)
    {
        return customersLoanFinanceInfoByPartnerservice.selectCustomersLoanFinanceInfoByPartner(customersLoanFinanceInfoByPartnerParam);
    }

    //获取统计数据
    @GetMapping("/totalLoanFinanceInfoByPartner")
    public ResultBean selectTotalLoanFinanceInfoByPartner(@RequestParam("partnerId") Long partnerId)
    {
        return customersLoanFinanceInfoByPartnerservice.selectTotalLoanFinanceInfoByPartner(partnerId);
    }

    //根据客户id获取主单信息
    @GetMapping("/getOrderByCustomerId")
    public ResultBean getOrderByCustomerId(@RequestParam("customerId") Long customerId)
    {
        return customersLoanFinanceInfoByPartnerservice.getOrderByCustomerId(customerId);
    }
}
