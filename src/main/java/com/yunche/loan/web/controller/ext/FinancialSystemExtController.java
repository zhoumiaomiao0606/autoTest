package com.yunche.loan.web.controller.ext;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.service.CustomersLoanFinanceInfoByPartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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


    /**
     * 代偿明细
     * @param param
     * @return
     */
    @PostMapping(value = "/compensationDetail",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean compensationDetail(@RequestBody CustomersLoanFinanceInfoByPartnerParam param){
        return  customersLoanFinanceInfoByPartnerservice.compensationDetail(param);
    }

    /**
     *返利明细-列表
     */
    @PostMapping(value = "/rebateDetailsList",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean rebateDetailsList(@RequestBody FSysRebateParam param){
        return customersLoanFinanceInfoByPartnerservice.rebateDetailsList(param);
    }

    /**
     *返利明细-详情
     */
    @PostMapping(value = "/rebateDetails",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean rebateDetails(@RequestBody FSysRebateParam param){

        return customersLoanFinanceInfoByPartnerservice.rebateDetails(param);
    }

    /**
     * 返利入账
     * @param param
     * @return
     */
    @PostMapping(value = "/rebateEnterAccount",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean rebateEnterAccount(@RequestBody FinancialRebateEnterAccountparam param){
        return customersLoanFinanceInfoByPartnerservice.rebateEnterAccount(param);
    }

    /**
     *返利明细-详情
     */
    @PostMapping(value = "/rebateDetailsefresh",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean rebateDetailsefresh(@RequestBody FSysRebateParam param){
        return customersLoanFinanceInfoByPartnerservice.rebateDetailsefresh(param);
    }

}
