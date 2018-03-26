package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.JpushService;
import com.yunche.loan.service.TaskSchedulingService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

//任务调度中心
@CrossOrigin
@RestController
@RequestMapping(value = {"/msg","/app/msg"})
public class MsgController {



    @Resource
    private JpushService jpushService;


    @GetMapping(value = "/list")
    public ResultBean list(@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        return jpushService.list(pageIndex,pageSize);
    }

    @GetMapping(value = "/read")
    public ResultBean read(@RequestParam Long id) {
        return ResultBean.ofSuccess(null);
    }
}
