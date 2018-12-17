package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.RemitDetailsDO;
import com.yunche.loan.domain.param.RemitDetailsParam;
import com.yunche.loan.domain.param.RemitSatusParam;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.FinanceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/finance")
public class FinanceController {

    @Resource
    private FinanceService financeService;
    /**
     * 财务详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id)
    {
        return ResultBean.ofSuccess(financeService.detail(Long.valueOf(order_id)));
    }

    @PostMapping(value = "/update")
    public ResultBean update(@RequestBody RemitDetailsParam remitDetailsParam)
    {
        return financeService.update(remitDetailsParam);
    }

    @GetMapping(value = "/payment")
    public ResultBean payment(@RequestParam Long orderId)
    {
        return financeService.payment(orderId);
    }

    @PostMapping(value = "/remitInfo")
    public ResultBean remitInfo(@RequestBody RemitSatusParam remitSatusParam)
    {
        return financeService.remitInfo(remitSatusParam);
    }
}
