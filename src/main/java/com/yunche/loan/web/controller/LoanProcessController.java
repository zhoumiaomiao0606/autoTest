package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.LoanProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消费贷-业务单流程
 * Created by zhouguoliang on 2018/1/30.
 */
@CrossOrigin
@RestController
@RequestMapping("/loanprocess")
public class LoanProcessController {

    @Autowired
    private LoanProcessService loanProcessService;


    /**
     * 通用审核接口      action： 0-打回 / 1-提交 / 2-弃单 / 3-资料增补
     * <p>
     *
     * @return
     */
    @PostMapping(value = "/approval", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> approval(@RequestBody ApprovalParam Approval) {
        return loanProcessService.approval(Approval);
    }

    /**
     * 当前业务单的当前任务节点
     *
     * @return
     */
    @GetMapping(value = "/task/current")
    public ResultBean<List<TaskStateVO>> currentTask(@RequestParam("orderId") Long orderId) {
        return loanProcessService.currentTask(orderId);
    }

    /**
     * 任务执行状态
     *
     * @param orderId
     * @param taskDefinitionKey
     * @return
     */
    @GetMapping(value = "/task/status")
    public ResultBean<TaskStateVO> currentTask(@RequestParam("orderId") Long orderId,
                                               @RequestParam("taskDefinitionKey") String taskDefinitionKey) {
        return loanProcessService.taskStatus(orderId, taskDefinitionKey);
    }

    /**
     * 日志    -订单生命周期      -从 Start -> End
     *
     * @param orderId
     * @param limit
     * @return
     */
    @GetMapping(value = "/history")
    public ResultBean<List<String>> orderHistory(@RequestParam("orderId") Long orderId,
                                                 @RequestParam(value = "limit", required = false) Integer limit) {
        return loanProcessService.orderHistory(orderId, limit);
    }
}


