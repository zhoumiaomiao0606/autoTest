package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.JpushService;
import com.yunche.loan.service.MsgService;
import com.yunche.loan.service.TaskSchedulingService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

//任务调度中心
@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/msg","/api/v1/app/msg"})
public class MsgController {



    @Resource
    private JpushService jpushService;

    @Resource
    private MsgService msgService;


    @GetMapping(value = "/list")
    public ResultBean list(@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        return jpushService.list(pageIndex,pageSize);
    }

    @GetMapping(value = "/read")
    public ResultBean read(@RequestParam Long id) {
        jpushService.read(id);
        return ResultBean.ofSuccess(null);
    }

    @GetMapping(value = "/creditDetail")
    public ResultBean creditDetail(@RequestParam Long orderId) {
        return ResultBean.ofSuccess(msgService.creditDetail(orderId));
    }

    @GetMapping(value = "/msgDetail")
    public ResultBean msgDetail(@RequestParam Long msgId) {
        return ResultBean.ofSuccess(msgService.msgDetail(msgId));
    }

}
