package com.yunche.loan.web.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.constant.ActivitiConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.LoanProcessLogVO;
import com.yunche.loan.domain.vo.LoanRejectLogVO;
import com.yunche.loan.domain.vo.TaskStateVO;
import com.yunche.loan.service.LoanProcessService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消费贷-业务单流程
 * Created by zhouguoliang on 2018/1/30.
 */
@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/loanprocess", "/api/v1/app/loanprocess"})
public class LoanProcessController {

    @Autowired
    private LoanProcessService loanProcessService;

    @Autowired
    private RuntimeService runtimeService;


    /**
     * 通用审核接口      action： 0-打回 / 1-提交 / 2-弃单 / 3-资料增补 / 4-新增任务 / 5-反审
     * <p>
     *
     * @return
     */
    @Limiter
    @PostMapping(value = "/approval", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> approval(@RequestBody ApprovalParam approval) {
        approval.setCheckPermission(true);
        approval.setNeedLog(true);
        approval.setNeedPush(true);
        return loanProcessService.approval(approval);
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
     * @param processId
     * @return
     */
    @GetMapping(value = "/task/status")
    public ResultBean<TaskStateVO> currentTask(@RequestParam("orderId") Long orderId,
                                               @RequestParam("taskDefinitionKey") String taskDefinitionKey,
                                               @RequestParam(value = "processId", required = false) Long processId) {
        return loanProcessService.taskStatus(orderId, taskDefinitionKey, processId);
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

    /**
     * 单个节点 审核日志详情（action、info等）
     *
     * @param orderId
     * @param taskDefinitionKey
     * @return
     */
    @GetMapping(value = "/log")
    public ResultBean<LoanProcessLogVO> log(@RequestParam("orderId") Long orderId,
                                            @RequestParam("taskDefinitionKey") String taskDefinitionKey) {
        return loanProcessService.log(orderId, taskDefinitionKey);
    }

    /**
     * 打回日志
     *
     * @param orderId
     * @param taskDefinitionKey
     * @return
     */
    @GetMapping(value = "/rejectLog")
    public ResultBean<LoanRejectLogVO> rejectLog(@RequestParam("orderId") Long orderId,
                                                 @RequestParam("taskDefinitionKey") String taskDefinitionKey) {
        return loanProcessService.rejectLog(orderId, taskDefinitionKey);
    }

    @GetMapping(value = "/startProcess_")
    public ResultBean<Map> startProcess_(@RequestParam("processKey") String processDefinitionKey) {

        // 开启activiti流程
        ProcessInstance processInstance = null;
        if (ActivitiConst.LOAN_PROCESS_KEY.equals(processDefinitionKey)) {
            processInstance = runtimeService.startProcessInstanceByKey(ActivitiConst.LOAN_PROCESS_KEY);
        } else if (ActivitiConst.LOAN_PROCESS_INSTEAD_PAY_KEY.equals(processDefinitionKey)) {
            processInstance = runtimeService.startProcessInstanceByKey(ActivitiConst.LOAN_PROCESS_INSTEAD_PAY_KEY);
        } else if (ActivitiConst.LOAN_PROCESS_COLLECTION_KEY.equals(processDefinitionKey)) {
            processInstance = runtimeService.startProcessInstanceByKey(ActivitiConst.LOAN_PROCESS_COLLECTION_KEY);
        } else if (ActivitiConst.LOAN_PROCESS_LEGAL_KEY.equals(processDefinitionKey)) {
            processInstance = runtimeService.startProcessInstanceByKey(ActivitiConst.LOAN_PROCESS_LEGAL_KEY);
        } else if (ActivitiConst.LOAN_PROCESS_BRIDGE_KEY.equals(processDefinitionKey)) {
            processInstance = runtimeService.startProcessInstanceByKey(ActivitiConst.LOAN_PROCESS_BRIDGE_KEY);
        }

        Preconditions.checkNotNull(processInstance, "开启流程实例异常");
        Preconditions.checkNotNull(processInstance.getProcessInstanceId(), "开启流程实例异常");

        List<ExecutionEntityImpl> executions = ((ExecutionEntityImpl) processInstance).getExecutions();
        ExecutionEntityImpl executionEntity = executions.get(0);

        Map<String, Object> map = Maps.newHashMap();

        map.put("id", executionEntity.getId());
        map.put("name", executionEntity.getName());
        map.put("description", executionEntity.getDescription());
        map.put("businessKey", executionEntity.getBusinessKey());

        map.put("startTime", executionEntity.getStartTime());
        map.put("processVariables", executionEntity.getProcessVariables());

        map.put("processInstanceId", executionEntity.getProcessInstanceId());
        map.put("processDefinitionId", executionEntity.getProcessDefinitionId());
        map.put("processDefinitionKey", executionEntity.getProcessDefinitionKey());
        map.put("processDefinitionVersion", executionEntity.getProcessDefinitionVersion());

        return ResultBean.ofSuccess(map);
    }

}