package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.JpushService;
import com.yunche.loan.service.TaskSchedulingService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

//任务调度中心
@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/taskscheduling","/api/v1/app/taskscheduling"})
public class TaskSchedulingController {

    @Resource
    private TaskSchedulingService taskSchedulingService;


    /**
     * 待办任务列表
     */
    @GetMapping(value = "/scheduletasklist")
    public ResultBean scheduleTaskList(@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        return taskSchedulingService.scheduleTaskList(pageIndex,pageSize);
    }

}
