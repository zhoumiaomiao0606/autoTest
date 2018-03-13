package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/loanorder/finance")
public class FinanceController {
    /**
     * 财务详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return null;
    }
}
