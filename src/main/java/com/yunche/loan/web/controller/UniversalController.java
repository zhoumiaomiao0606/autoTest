package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.Postcode;
import com.yunche.loan.service.ChinapostFeignClient;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/universal")
public class UniversalController {

    @Resource
    private LoanQueryService loanQueryService;

    @Resource
    private ChinapostFeignClient chinapostFeignClient;

    @GetMapping(value = "/customer")
    public ResultBean customerDetail(@RequestParam String customer_id) {

        return ResultBean.ofSuccess(loanQueryService.universalCustomerDetail(Long.valueOf(customer_id)));
    }

    @GetMapping(value = "/postcode")
    public ResultBean postcode(@RequestParam String address) {
        Postcode postcode = chinapostFeignClient.getPostcodeData(address);
        return ResultBean.ofSuccess(postcode);
    }
}
