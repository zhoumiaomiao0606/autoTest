package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.LoanProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 消费贷-业务单流程
 * Created by zhouguoliang on 2018/1/30.
 */
@CrossOrigin
@RestController
@RequestMapping("/loanprocess")
public class LoanProcessController {

    private static final Logger logger = LoggerFactory.getLogger(LoanProcessController.class);

    @Autowired
    private LoanProcessService loanProcessService;


    /**
     * 生成消费贷-流程ID  （业务单编号）
     *
     * @return
     */
    @GetMapping(value = "/getOrderId")
    public ResultBean<String> getOrderId() {
        return loanProcessService.getOrderId();
    }

    /**
     * 创建征信申请单 -【开启流程】
     *
     * @param creditApplyVO
     * @return 业务单ID
     */
    @PostMapping(value = "/creditapply/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<String> create(@RequestBody CreditApplyVO creditApplyVO) {
        return loanProcessService.createCreditApply(creditApplyVO);
    }

    /**
     * 编辑征信申请单
     *
     * @param processInstOrder
     * @return
     */
    @PostMapping(value = "/creditapply/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> create(@RequestBody InstProcessOrderVO processInstOrder) {
        return loanProcessService.updateCreditApply(processInstOrder);
    }

    /**
     * 通用审核接口      action： 0-打回 / 1-提交 / 2-弃单 / 3-资料增补
     * <p>
     *
     * @return
     */
    @PostMapping(value = "/approval", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> approvalCreditApply(@RequestBody ApprovalParam Approval) {
        logger.info("/loanprocess/approval", JSON.toJSONString(Approval));
        return loanProcessService.approval(Approval);
    }

    /**
     * 当前业务单的当前任务节点
     *
     * @return
     */
    @GetMapping(value = "/task/current")
    public ResultBean<TaskStateVO> currentTask(@RequestParam("orderId") String orderId) {
        logger.info("/loanprocess/task/current", JSON.toJSONString(orderId));
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
    public ResultBean<Byte> currentTask(@RequestParam("orderId") String orderId,
                                        @RequestParam("taskDefinitionKey") String taskDefinitionKey) {
        logger.info("/loanprocess/task/status", JSON.toJSONString(orderId), JSON.toJSONString(taskDefinitionKey));
        return loanProcessService.taskStatus(orderId, taskDefinitionKey);
    }
}


