package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.yunche.loan.config.constant.ActivitiConst.LOAN_PROCESS_COLLECTION_KEY;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;
import static com.yunche.loan.config.constant.ProcessApprovalConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.*;

/**
 * @author liuzhe
 * @date 2018/8/20
 */
@Service
public class LoanProcessInsteadPayServiceImpl implements LoanProcessInsteadPayService {

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanProcessInsteadPayDOMapper loanProcessInsteadPayDOMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ActivitiService activitiService;

    @Autowired
    private LoanProcessApprovalCommonService loanProcessApprovalCommonService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBean<Void> approval(ApprovalParam approval) {
        Preconditions.checkNotNull(approval.getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(approval.getAction(), "审核结果不能为空");
        Preconditions.checkNotNull(approval.getProcessId(), "processId不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(approval.getTaskDefinitionKey()), "执行任务不能为空");

        // 节点权限校验
        if (approval.isCheckPermission()) {
            permissionService.checkTaskPermission(approval.getTaskDefinitionKey());
        }

        // 业务单
        LoanOrderDO loanOrderDO = getLoanOrder(approval.getOrderId());

        // 节点实时状态
        LoanProcessInsteadPayDO loanProcessDO = getLoanProcess(approval.getProcessId());

        // 贷款基本信息
//        LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());

        // 校验审核前提条件
//        checkPreCondition(approval.getTaskDefinitionKey(), approval.getAction(), loanOrderDO, loanProcessInsteadPayDO);

        // 日志
        loanProcessApprovalCommonService.log(approval);

        // 获取当前执行任务（activiti中）
        Task task = loanProcessApprovalCommonService.getTask(loanProcessDO.getProcessInstId(), approval.getTaskDefinitionKey());

        // 先获取提交之前的待执行任务ID列表
        List<String> startTaskIdList = loanProcessApprovalCommonService.getCurrentTaskIdList(task.getProcessInstanceId());

        // 流程变量
        Map<String, Object> variables = setAndGetVariables(approval);

        // 执行任务
        execTask(task, variables);

        // 流程数据同步
        syncProcess(startTaskIdList, loanProcessDO.getProcessInstId(), approval, loanProcessDO);

        // 生成客户还款计划
//        createRepayPlan(approval.getTaskDefinitionKey(), loanProcessDO, loanOrderDO);

        // [领取]完成
        loanProcessApprovalCommonService.finishTask(approval, startTaskIdList, loanOrderDO.getProcessInstId());

        // 通过银行接口  ->  自动查询征信
//        creditAutomaticCommit(approval);

        // 异步打包文件
//        asyncPackZipFile(approval.getTaskDefinitionKey(), loanProcessDO, 2);

        // 异步推送
        loanProcessApprovalCommonService.asyncPush(loanOrderDO, approval);

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.getNameByCode(approval.getOriginalTaskDefinitionKey()) + "]任务执行成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long startProcess(@NotNull(message = "orderId不能为空") Long orderId,
                             @NotNull(message = "insteadPayOrderId不能为空") Long insteadPayOrderId) {

        ProcessInstance processInstance = activitiService.startProcessInstanceByKey(LOAN_PROCESS_COLLECTION_KEY);

        // 创建流程记录
        Long processId = create(orderId, insteadPayOrderId, processInstance.getProcessInstanceId());

        return processId;
    }

    /**
     * 创建[催收工作台]流程记录
     *
     * @param orderId
     * @param insteadPayOrderId
     * @param processInstId
     * @return
     */
    private Long create(Long orderId, Long insteadPayOrderId, String processInstId) {

        LoanProcessInsteadPayDO loanProcessInsteadPayDO = new LoanProcessInsteadPayDO();

        loanProcessInsteadPayDO.setOrderId(orderId);
        loanProcessInsteadPayDO.setInsteadPayOrderId(insteadPayOrderId);
        loanProcessInsteadPayDO.setProcessInstId(processInstId);

        loanProcessInsteadPayDO.setApplyInsteadPay(TASK_PROCESS_TODO);

        loanProcessInsteadPayDO.setGmtCreate(new Date());
        loanProcessInsteadPayDO.setGmtModify(new Date());

        int count = loanProcessInsteadPayDOMapper.insertSelective(loanProcessInsteadPayDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return loanProcessInsteadPayDO.getId();
    }

    /**
     * 获取业务单
     *
     * @param orderId
     * @return
     */
    public LoanOrderDO getLoanOrder(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");
        Preconditions.checkNotNull(loanOrderDO.getProcessInstId(), "流程实例ID不存在");

        return loanOrderDO;
    }

    /**
     * 获取 订单流程节点 实时状态记录
     *
     * @param processId
     * @return
     */
    private LoanProcessInsteadPayDO getLoanProcess(Long processId) {
        LoanProcessInsteadPayDO loanProcessInsteadPayDO = loanProcessInsteadPayDOMapper.selectByPrimaryKey(processId);
        Preconditions.checkNotNull(loanProcessInsteadPayDO, "流程记录丢失");

        return loanProcessInsteadPayDO;
    }

    /**
     * 获取当前执行任务（activiti中）
     *
     * @param processInstId
     * @param taskDefinitionKey
     * @return
     */
    public Task getTask(String processInstId, String taskDefinitionKey) {

        // 获取当前流程task
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstId)
                .taskDefinitionKey(taskDefinitionKey)
                .singleResult();

        Preconditions.checkNotNull(task, "当前任务不存在");

        return task;
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
     * @param startTaskIdList      起始任务ID列表
     * @param processInstId
     * @param approval
     * @param currentLoanProcessDO
     */
    private void syncProcess(List<String> startTaskIdList, String processInstId, ApprovalParam approval,
                             LoanProcessInsteadPayDO currentLoanProcessDO) {

        // 更新状态
        LoanProcessInsteadPayDO loanProcessDO = new LoanProcessInsteadPayDO();
        loanProcessDO.setId(approval.getProcessId());
        loanProcessDO.setOrderId(approval.getOrderId());

//        // 如果弃单，则记录弃单节点
//        if (ACTION_CANCEL.equals(approval.getAction())) {
//            loanProcessDO.setOrderStatus(ORDER_STATUS_CANCEL);
//            loanProcessDO.setCancelTaskDefKey(approval.getTaskDefinitionKey());
//            updateCurrentTaskProcessStatus(loanProcessDO, approval.getTaskDefinitionKey(), TASK_PROCESS_CANCEL, approval);
//        }

        // 结单 ending  -暂无【结单节点】
//        if (XXX.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {
//            loanProcessDO.setOrderStatus(ORDER_STATUS_END);
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
        updateNextTaskProcessStatus(loanProcessDO, processInstId, startTaskIdList, approval, currentLoanProcessDO);

        // 特殊处理：部分节点的同步  !!!
//        special_syncProcess(approval, loanProcessDO, currentLoanProcessDO, loanBaseInfoDO);

        // 更新本地流程记录
        updateLoanProcess(loanProcessDO);
    }

    /**
     * 更新本地流程记录
     *
     * @param loanProcessDO
     */
    private void updateLoanProcess(LoanProcessInsteadPayDO loanProcessDO) {
        loanProcessDO.setGmtModify(new Date());
        int count = loanProcessInsteadPayDOMapper.updateByPrimaryKeySelective(loanProcessDO);
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
    private void updateCurrentTaskProcessStatus(LoanProcessInsteadPayDO loanProcessDO, String taskDefinitionKey,
                                                Byte taskProcessStatus, ApprovalParam approval) {

        if (null == taskProcessStatus) {
            return;
        }

        if (taskDefinitionKey.startsWith("filter")) {
            return;
        }

        // 更新资料流转type
//        doUpdateDataFlowType(loanProcessDO, taskDefinitionKey, taskProcessStatus, approval);

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
     * @param currentLoanProcessDO
     */
    private void updateNextTaskProcessStatus(LoanProcessInsteadPayDO loanProcessDO, String processInstanceId, List<String> startTaskIdList,
                                             ApprovalParam approval, LoanProcessInsteadPayDO currentLoanProcessDO) {

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

//            // 是否已过电审
//            if (!TASK_PROCESS_DONE.equals(currentLoanProcessDO.getTelephoneVerify())) {
//                // 没过电审
//                doUpdateNextTaskProcessStatus(oldTaskList, loanProcessDO, TASK_PROCESS_INIT, approval);
//            } else {
//                // 过了电审，则不是真正的全部打回      nothing
//            }

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
    private void doUpdateNextTaskProcessStatus(List<Task> nextTaskList, LoanProcessInsteadPayDO loanProcessDO,
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

        return variables;
    }

}