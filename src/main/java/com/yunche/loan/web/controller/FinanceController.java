package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.FinanceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/loanorder/finance")
public class FinanceController {

    @Resource
    private FinanceService financeService;
    /**
     * 财务详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return ResultBean.ofSuccess(financeService.detail(Long.valueOf(order_id)));
    }
}
