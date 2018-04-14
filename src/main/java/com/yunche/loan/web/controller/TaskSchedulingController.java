package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.AppTaskVO;
import com.yunche.loan.service.TaskSchedulingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 任务调度中心
 */
@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/taskscheduling", "/api/v1/app/taskscheduling"})
public class TaskSchedulingController {

    @Resource
    private TaskSchedulingService taskSchedulingService;


    /**
     * 待办任务列表
     */
    @GetMapping(value = "/scheduletasklist")
    public ResultBean scheduleTaskList(@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        return taskSchedulingService.scheduleTaskList(pageIndex, pageSize);
    }

    /**
     * 查询接口
     */
    @PostMapping(value = "/queryTaskList")
    public ResultBean scheduleTaskList(@RequestBody @Validated TaskListQuery taskListQuery) {
        return taskSchedulingService.queryTaskList(taskListQuery);
    }

    /**
     * 查询接口
     */
    @PostMapping(value = "/queryAppTaskList")
    public ResultBean<List<AppTaskVO>> queryAppTaskList(@RequestBody @Validated AppTaskListQuery appTaskListQuery) {
        return taskSchedulingService.queryAppTaskList(appTaskListQuery);
    }


    /**
     * 查询接口
     */
    @PostMapping(value = "/queryLoginUserLevel")
    public ResultBean queryLoginUserLevel() {
        return taskSchedulingService.queryLoginUserLevel();
    }
}
