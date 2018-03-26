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
@RequestMapping("/api/v1/loanprocess")
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
    public ResultBean<Integer> currentTask(@RequestParam("orderId") Long orderId,
                                           @RequestParam("taskDefinitionKey") String taskDefinitionKey) {
        return loanProcessService.taskStatus(orderId, taskDefinitionKey);
    }
}


