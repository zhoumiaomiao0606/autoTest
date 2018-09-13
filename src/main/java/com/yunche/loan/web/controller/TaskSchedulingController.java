package com.yunche.loan.web.controller;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.FlowOperationMsgParam;
import com.yunche.loan.domain.param.TaskDistributionParam;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.query.ZhonganListQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.TaskDistributionService;
import com.yunche.loan.service.TaskSchedulingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
     * 领取
     */
    @GetMapping(value = "/appCount")
    public ResultBean<Map> appCount() {
        return taskSchedulingService.appCount();
    }

    /**
     * 领取
     */
    @PostMapping(value = "/flowOperationMsgList")
    public ResultBean<List<FlowOperationMsgListVO>> flowOperationMsgList(@RequestBody @Validated FlowOperationMsgParam param) {
        return taskSchedulingService.selectFlowOperationMsgList(param);
    }

    /**
     * 领取
     */
    @PostMapping(value = "/zhonganList")
    public ResultBean<List<ZhonganListVO>> zhonganList(@RequestBody @Validated ZhonganListQuery param) {
        return taskSchedulingService.selectZhonganList(param);
    }

    /**
     * 是否属于银行单子
     */
    @GetMapping(value = "/canUpdateLoanApply")
    public ResultBean<Boolean> canUpdateLoanApply(@RequestParam Long orderId) {
        return ResultBean.ofSuccess(taskSchedulingService.selectRejectTask(orderId));
    }

    /**
     * 是否属于银行单子
     */
    @GetMapping(value = "/isBankOrder")
    public ResultBean<Boolean> isBankOrder(@RequestParam Long orderId, @RequestParam String transCode) {
        return ResultBean.ofSuccess(loanQueryService.selectCheckOrderInBankInterfaceSerial(orderId, transCode));
    }

    /**
     * 银行单子状态
     */
    @GetMapping(value = "/bankOrderStatus")
    public ResultBean<Integer> bankOrderStatus(@RequestParam Long orderId, @RequestParam String transCode) {
        return ResultBean.ofSuccess(loanQueryService.selectBankInterFaceSerialOrderStatusByOrderId(orderId, transCode));
    }

    /**
     * 是否待电审
     */
    @GetMapping(value = "/isTelephoneVerify")
    public ResultBean<Integer> isTelephoneVerify(@RequestParam Long orderId) {
        return ResultBean.ofSuccess(loanQueryService.selectBankOpenCardStatusByOrderId(orderId));
    }

    /**
     * 是否待电审
     */
    @GetMapping(value = "/bankOrderApiMsg")
    public ResultBean<String> selectLastBankInterfaceSerialMsgByTransCode(@RequestParam Long customerId, @RequestParam String transCode) {
        return ResultBean.ofSuccess(loanQueryService.selectLastBankInterfaceSerialNoteByTransCode(customerId, transCode));
    }

    /**
     * 是否待电审
     */
    @GetMapping(value = "/bankOrder")
    public ResultBean<BankInterfaceSerialReturnVO> selectLastBankInterfaceSerialByTransCode(@RequestParam Long customerId, @RequestParam String transCode) {
        return ResultBean.ofSuccess(loanQueryService.selectLastBankInterfaceSerialByTransCode(customerId, transCode));
    }

    /**
     * 待办任务列表-all
     */
    @Limiter(2)
    @GetMapping(value = "/scheduletasklist")
    public ResultBean<List<ScheduleTaskVO>> scheduletasklist(@RequestParam(required = false) String key,
                                                             @RequestParam Integer pageIndex,
                                                             @RequestParam Integer pageSize) {
        return taskSchedulingService.scheduleTaskList(key, pageIndex, pageSize);
    }

    /**
     * 待办任务列表-all
     */
    @Limiter(2)
    @GetMapping(value = "/countScheduletasklist")
    public ResultBean<Long> countScheduletasklist() {
        return taskSchedulingService.countScheduletasklist();
    }


    /**
     * 查询接口
     */
//    @Limiter(2)
    @PostMapping(value = "/queryTaskList")
    public ResultBean<List<TaskListVO>> scheduleTaskList(@RequestBody @Validated TaskListQuery taskListQuery) {
        return taskSchedulingService.queryTaskList(taskListQuery);
    }

    /**
     * 查询接口
     */
    @Limiter(2)
    @PostMapping(value = "/countQueryTaskList")
    public ResultBean<Long> countQueryTaskList(@RequestBody @Validated TaskListQuery taskListQuery) {
        return taskSchedulingService.countQueryTaskList(taskListQuery);
    }

    /**
     * 查询接口
     */
    @Limiter(2)
    @PostMapping(value = "/queryAppTaskList")
    public ResultBean<List<AppTaskVO>> queryAppTaskList(@RequestBody @Validated AppTaskListQuery appTaskListQuery) {
        return taskSchedulingService.queryAppTaskList(appTaskListQuery);
    }

    @GetMapping(value = "/queryLoginUserLevel")
    public ResultBean<String> queryLoginUserLevel() {
        return ResultBean.ofSuccess(loanQueryService.selectTelephoneVerifyLevel());
    }

    /**
     * 领取任务             --insert
     */
    @PostMapping(value = "/get")
    public ResultBean<Void> get(@RequestBody @Validated TaskDistributionParam param) {
        taskDistributionService.get(Long.valueOf(param.getTaskId()), param.getTaskKey());
        return ResultBean.ofSuccess(null, "操作成功");
    }


    /**
     * 取消领取  -释放任务    --delete
     */
    @PostMapping(value = "/release")
    public ResultBean<Void> release(@RequestBody @Validated TaskDistributionParam param) {
        taskDistributionService.release(Long.valueOf(param.getTaskId()), param.getTaskKey());
        return ResultBean.ofSuccess(null, "操作成功");
    }

    /**
     * query
     */
    @PostMapping(value = "/query")
    public ResultBean<TaskDisVO> query(@RequestBody @Validated TaskDistributionParam param) {
        TaskDisVO taskDisVO = taskDistributionService.query(Long.valueOf(param.getTaskId()), param.getTaskKey());
        return ResultBean.ofSuccess(taskDisVO, "操作成功");
    }
}
