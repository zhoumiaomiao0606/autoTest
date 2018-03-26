package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.FinancialSchemeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/financialscheme")
public class FinancialSchemeController {
    @Resource
    private FinancialSchemeService financialSchemeService;
    /**
     * 金融方案详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return ResultBean.ofSuccess(financialSchemeService.detail(Long.valueOf(order_id)));
    }

}
