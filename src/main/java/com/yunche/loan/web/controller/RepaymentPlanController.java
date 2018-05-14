package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.RepaymentPlanService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/repaymentplan")
public class RepaymentPlanController {

    @Resource
    private RepaymentPlanService repaymentPlanService;

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id){
        return ResultBean.ofSuccess(repaymentPlanService.detail(Long.valueOf(order_id)));
    }

}
