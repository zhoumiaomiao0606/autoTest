package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.TaskDistributionDO;
import com.yunche.loan.domain.param.TaskDistributionParam;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.AppTaskVO;
import com.yunche.loan.domain.vo.TaskDisVO;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.TaskDistributionService;
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

    @Resource
    private TaskDistributionService taskDistributionService;

    @Resource
    private LoanQueryService loanQueryService;

    /**
     * 待办任务列表-all
     */
    @GetMapping(value = "/scheduletasklist")
    public ResultBean scheduletasklist(@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        return taskSchedulingService.scheduleTaskList(pageIndex,pageSize);
    }

    /**
     * 待办任务列表
     */
    @GetMapping(value = "/scheduletasklistBykey")
    public ResultBean scheduletasklistBykey(@RequestParam String key,@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        return taskSchedulingService.scheduleTaskListBykey(key,pageIndex, pageSize);
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

    @GetMapping(value = "/queryLoginUserLevel")
    public ResultBean queryLoginUserLevel() {
        return ResultBean.ofSuccess(loanQueryService.selectTelephoneVerifyLevel());
    }

    /**
     * 领取
     */
    @PostMapping(value = "/get")
    public ResultBean<Void> get(@RequestBody @Validated TaskDistributionParam param) {
         taskDistributionService.get(Long.valueOf(param.getTaskId()),param.getTaskKey());
        return ResultBean.ofSuccess(null,"操作成功");

    }


    /**
     * 释放
     */
    @PostMapping(value = "/release")
    public ResultBean<Void> release(@RequestBody @Validated TaskDistributionParam param) {
         taskDistributionService.release(Long.valueOf(param.getTaskId()),param.getTaskKey());
        return ResultBean.ofSuccess(null,"操作成功");
    }

    /**
     * query
     */
    @PostMapping(value = "/query")
    public ResultBean query(@RequestBody @Validated TaskDistributionParam param) {
        TaskDisVO taskDisVO = taskDistributionService.query(Long.valueOf(param.getTaskId()),param.getTaskKey());
        return ResultBean.ofSuccess(taskDisVO,"操作成功");
    }
}
