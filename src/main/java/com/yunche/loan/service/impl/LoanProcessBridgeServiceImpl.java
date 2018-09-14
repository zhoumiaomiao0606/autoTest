package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.mapper.LoanProcessBridgeDOMapper;
import com.yunche.loan.service.*;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.yunche.loan.config.constant.ActivitiConst.LOAN_PROCESS_BRIDGE_KEY;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.SETTLE_ORDER;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_ACTION;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_TARGET;
import static com.yunche.loan.config.constant.ProcessApprovalConst.*;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_CANCEL;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_PASS;

/**
 * @author liuzhe
 * @date 2018/9/11
 */
@Service
public class LoanProcessBridgeServiceImpl implements LoanProcessBridgeService {


    @Autowired
    private LoanProcessBridgeDOMapper loanProcessBridgeDOMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ActivitiService activitiService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private LoanProcessApprovalCommonService loanProcessApprovalCommonService;

    @Autowired
    private LoanProcessApprovalRollBackService loanProcessApprovalRollBackService;


    @Override
    @Transactional
    public ResultBean<Void> approval(ApprovalParam approval) {
        Preconditions.checkNotNull(approval.getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(approval.getAction(), "审核结果不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(approval.getTaskDefinitionKey()), "执行任务不能为空");

        // 节点权限校验
        if (approval.isCheckPermission()) {
            permissionService.checkTaskPermission(approval.getTaskDefinitionKey());
        }

        // 订单状态校验
        loanProcessApprovalCommonService.checkOrderStatus(approval.getOrderId());

        // 操作日志
        loanProcessApprovalCommonService.log(approval);

        // 业务单
        LoanOrderDO loanOrderDO = loanProcessApprovalCommonService.getLoanOrder(approval.getOrderId());

        // 节点实时状态
        LoanProcessBridgeDO loanProcessDO = getLoanProcess(approval.getOrderId());

        if (ACTION_ROLL_BACK.equals(approval.getAction())) {
            return loanProcessApprovalRollBackService.execRollBackTask(approval, loanOrderDO, loanProcessDO);
        }

        // 获取当前执行任务（activiti中）
        Task task = loanProcessApprovalCommonService.getTask(loanProcessDO.getProcessInstId(), approval.getTaskDefinitionKey());

        // 先获取提交之前的待执行任务ID列表
        List<String> startTaskIdList = loanProcessApprovalCommonService.getCurrentTaskIdList(task.getProcessInstanceId());

        // 流程变量
        Map<String, Object> variables = setAndGetVariables(approval);

        // 执行任务
        execTask(task, variables);

        // 流程数据同步
        syncProcess(startTaskIdList, loanProcessDO.getProcessInstId(), approval);

        // [领取]完成
        loanProcessApprovalCommonService.finishTask(approval, startTaskIdList, loanProcessDO.getProcessInstId());

        // 异步推送
        loanProcessApprovalCommonService.asyncPush(loanOrderDO, approval);

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.getNameByCode(approval.getOriginalTaskDefinitionKey()) + "]任务执行成功");
    }

    @Override
    @Transactional
    public String startProcess(Long orderId) {
        Preconditions.checkNotNull(orderId, "orderId不能为空");

        LoanProcessBridgeDO loanProcessBridgeDO = loanProcessBridgeDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkArgument(null == loanProcessBridgeDO, "[第三方过桥资金]流程记录已存在");

        // 开启activiti流程  -上门催收
        ProcessInstance processInstance = activitiService.startProcessInstanceByKey(LOAN_PROCESS_BRIDGE_KEY);

        // 创建流程记录
        loanProcessBridgeDO = new LoanProcessBridgeDO();
        loanProcessBridgeDO.setOrderId(orderId);
        loanProcessBridgeDO.setProcessInstId(processInstance.getProcessInstanceId());
        loanProcessBridgeDO.setBridgeHandle(TASK_PROCESS_TODO);

        loanProcessBridgeDO.setGmtCreate(new Date());
        loanProcessBridgeDO.setGmtModify(new Date());

        // insert
        int count = loanProcessBridgeDOMapper.insertSelective(loanProcessBridgeDO);
        Preconditions.checkArgument(count > 0, "流程记录失败");

        return loanProcessBridgeDO.getProcessInstId();
    }

    /**
     * 获取 订单流程节点 实时状态记录
     *
     * @param orderId
     * @return
     */
    private LoanProcessBridgeDO getLoanProcess(Long orderId) {
        LoanProcessBridgeDO loanProcessDO = loanProcessBridgeDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

        return loanProcessDO;
    }

    /**
     * 执行任务
     *
     * @param task
     * @param variables
     */
    private void execTask(Task task, Map<String, Object> variables) {

        // 其他任务：直接提交
        completeTask(task.getId(), variables);
    }

    /**
     * 完成任务   ==>   在activiti中完成
     *
     * @param taskId
     * @param variables
     */
    private void completeTask(String taskId, Map<String, Object> variables) {
        // 执行任务
        taskService.complete(taskId, variables);
    }

    /**
     * 流程数据同步： 同步activiti与本地流程数据
     *
     * @param startTaskIdList 起始任务ID列表
     * @param processInstId
     * @param approval
     */
    private void syncProcess(List<String> startTaskIdList, String processInstId, ApprovalParam approval) {

        // 更新状态
        LoanProcessBridgeDO loanProcessDO = new LoanProcessBridgeDO();
        loanProcessDO.setOrderId(approval.getOrderId());

//        // 如果弃单，则记录弃单节点
//        if (ACTION_CANCEL.equals(approval.getAction())) {
//            loanProcessDO.setOrderStatus(ORDER_STATUS_CANCEL);
//            loanProcessDO.setCancelTaskDefKey(approval.getTaskDefinitionKey());
//            updateCurrentTaskProcessStatus(loanProcessDO, approval.getTaskDefinitionKey(), TASK_PROCESS_CANCEL, approval);
//        }

        // 更新当前执行的任务状态
        Byte taskProcessStatus = null;
        if (ACTION_REJECT_MANUAL.equals(approval.getAction()) || ACTION_REJECT_AUTO.equals(approval.getAction())) {
            taskProcessStatus = TASK_PROCESS_INIT;
        } else if (ACTION_PASS.equals(approval.getAction()) && !TELEPHONE_VERIFY.getCode().equals(approval.getTaskDefinitionKey())) {
            // Tips：[电审]通过 状态更新不走这里
            taskProcessStatus = TASK_PROCESS_DONE;
        }
        updateCurrentTaskProcessStatus(loanProcessDO, approval.getTaskDefinitionKey(), taskProcessStatus, approval);

        // 更新新产生的任务状态
        updateNextTaskProcessStatus(loanProcessDO, processInstId, startTaskIdList, approval);

        // 更新本地流程记录
        updateLoanProcess(loanProcessDO);
    }

    /**
     * 更新本地流程记录
     *
     * @param loanProcessDO
     */
    private void updateLoanProcess(LoanProcessBridgeDO loanProcessDO) {
        loanProcessDO.setGmtModify(new Date());
        int count = loanProcessBridgeDOMapper.updateByPrimaryKeySelective(loanProcessDO);
        Preconditions.checkArgument(count > 0, "更新本地流程记录失败");
    }

    /**
     * 更新本地已执行的任务状态
     *
     * @param loanProcessDO
     * @param taskDefinitionKey
     * @param taskProcessStatus
     * @param approval
     */
    private void updateCurrentTaskProcessStatus(LoanProcessBridgeDO loanProcessDO, String taskDefinitionKey,
                                                Byte taskProcessStatus, ApprovalParam approval) {

        if (null == taskProcessStatus) {
            return;
        }

        if (taskDefinitionKey.startsWith("filter")) {
            return;
        }

        // 执行更新
        loanProcessApprovalCommonService.doUpdateCurrentTaskProcessStatus(loanProcessDO, taskDefinitionKey, taskProcessStatus);
    }

    /**
     * 更新新产生的任务状态
     *
     * @param loanProcessDO
     * @param processInstanceId
     * @param startTaskIdList
     * @param approval
     */
    private void updateNextTaskProcessStatus(LoanProcessBridgeDO loanProcessDO, String processInstanceId, List<String> startTaskIdList,
                                             ApprovalParam approval) {

        // 获取提交之后的待执行任务列表
        List<Task> endTaskList = loanProcessApprovalCommonService.getCurrentTaskList(processInstanceId);

        if (CollectionUtils.isEmpty(endTaskList)) {
            return;
        }

        // 筛选出新产生和旧有的任务
        List<Task> newTaskList = Lists.newArrayList();
        List<Task> oldTaskList = Lists.newArrayList();
        endTaskList.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {
                    if (!startTaskIdList.contains(e.getId())) {
                        // 不存在：新产生的任务
                        newTaskList.add(e);
                    } else {
                        // 存在：旧有的任务
                        oldTaskList.add(e);
                    }
                });

        Byte action = approval.getAction();
        if (ACTION_PASS.equals(action)) {
            // new  -> TO_DO   old -> 不变
            doUpdateNextTaskProcessStatus(newTaskList, loanProcessDO, TASK_PROCESS_TODO, approval);
        } else if (ACTION_REJECT_MANUAL.equals(action)) {
            // new  -> REJECT   old -> INIT
            doUpdateNextTaskProcessStatus(newTaskList, loanProcessDO, TASK_PROCESS_REJECT, approval);

            // 打回记录
            loanProcessApprovalCommonService.createRejectLog(newTaskList, approval.getOrderId(),
                    approval.getTaskDefinitionKey(), approval.getInfo());

        } else if (ACTION_CANCEL.equals(action)) {
            // nothing
        }
    }

    /**
     * 更新待执行的任务状态
     *
     * @param nextTaskList
     * @param loanProcessDO
     * @param taskProcessStatus 未提交/打回
     * @param approval
     */
    private void doUpdateNextTaskProcessStatus(List<Task> nextTaskList, LoanProcessBridgeDO loanProcessDO,
                                               Byte taskProcessStatus, ApprovalParam approval) {

        if (!CollectionUtils.isEmpty(nextTaskList)) {
            nextTaskList.stream()
                    .filter(Objects::nonNull)
                    .forEach(task -> {
                        // 未提交
                        updateCurrentTaskProcessStatus(loanProcessDO, task.getTaskDefinitionKey(), taskProcessStatus, approval);
                    });
        }
    }

    /**
     * 设置并返回流程变量
     *
     * @param approval
     */
    private Map<String, Object> setAndGetVariables(ApprovalParam approval) {
        Map<String, Object> variables = Maps.newHashMap();

        // 流程变量：action
        variables.put(PROCESS_VARIABLE_ACTION, approval.getAction());

//        fillOtherVariables(variables, approval);

        return variables;
    }


    /**
     * 填充其他的流程变量
     *
     * @param variables
     * @param approval
     */
    private void fillOtherVariables(Map<String, Object> variables, ApprovalParam approval) {

    }

}
