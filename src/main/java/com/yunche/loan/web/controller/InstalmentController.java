package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.InstalmentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/instalment")
public class InstalmentController {

    @Resource
    private InstalmentService instalmentService;

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String orderId) {
        return ResultBean.ofSuccess(instalmentService.detail(Long.valueOf(orderId)));
    }

}
