package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.TelephoneVerifyService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/telephoneverify"})
public class TelephoneVerifyController {

    @Resource
    private TelephoneVerifyService telephoneVerifyService;
    /**
     * 待办任务列表
     */
    @GetMapping(value = "/detail")
    public ResultBean scheduleTaskList(@RequestParam String order_id) {
        return ResultBean.ofSuccess(telephoneVerifyService.detail(Long.valueOf(order_id)));
    }

}
