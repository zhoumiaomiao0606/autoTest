package com.yunche.loan.web.controller.ext.financial;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CustomerInfoByCustomerNameParam;
import com.yunche.loan.domain.param.CustomersLoanFinanceInfoByPartnerParam;
import com.yunche.loan.domain.param.RefundOrderInfoByPartnerParam;
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

    //根据客户id获取主单信息
    @GetMapping("/getOrderByOrderId")
    public ResultBean getOrderByOrderId(@RequestParam("orderId") Long orderId)
    {
        return customersLoanFinanceInfoByPartnerservice.getOrderByOrderId(orderId);
    }

    //模糊查询客户
    @PostMapping("/getCustomerInfoByCustomerName")
    public ResultBean getCustomerInfoByCustomerName(@RequestBody CustomerInfoByCustomerNameParam customerInfoByCustomerNameParam)
    {
        return customersLoanFinanceInfoByPartnerservice.getCustomerInfoByCustomerName(customerInfoByCustomerNameParam);
    }

    //根据合伙人获取列表信息----退单未返款
    @PostMapping("/refundOrderInfoByPartner")
    public ResultBean selectRefundOrderInfoByPartner(@RequestBody RefundOrderInfoByPartnerParam refundOrderInfoByPartnerParam)
    {
        return customersLoanFinanceInfoByPartnerservice.selectRefundOrderInfoByPartner(refundOrderInfoByPartnerParam);
    }

}
