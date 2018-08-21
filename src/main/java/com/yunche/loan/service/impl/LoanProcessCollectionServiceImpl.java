package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.StringUtil;
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
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.yunche.loan.config.constant.ActivitiConst.LOAN_PROCESS_COLLECTION_KEY;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;
import static com.yunche.loan.config.constant.ProcessApprovalConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_ACTION;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_TARGET;

/**
 * @author liuzhe
 * @date 2018/8/20
 */
@Service
public class LoanProcessCollectionServiceImpl implements LoanProcessCollectionService {

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanProcessCollectionDOMapper loanProcessCollectionDOMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ActivitiService activitiService;

    @Autowired
    private PermissionService permissionService;

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
        LoanProcessCollectionDO loanProcessDO = getLoanProcess(approval.getProcessId());

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
        Map<String, Object> variables = setAndGetVariables(approval, loanProcessDO);

        // 执行任务
        execTask(task, variables);

        // 流程数据同步
        syncProcess(startTaskIdList, loanProcessDO.getProcessInstId(), approval, loanProcessDO);

        // 生成客户还款计划
//        createRepayPlan(approval.getTaskDefinitionKey(), loanProcessDO, loanOrderDO);

        // [领取]完成
        loanProcessApprovalCommonService.finishTask(approval, startTaskIdList, loanProcessDO.getProcessInstId());

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
                             @NotNull(message = "collectionOrderId不能为空") Long collectionOrderId) {

        // 开启activiti流程
        ProcessInstance processInstance = activitiService.startProcessInstanceByKey(LOAN_PROCESS_COLLECTION_KEY);

        // 创建流程记录
        Long processId = create(orderId, collectionOrderId, processInstance.getProcessInstanceId());

        return processId;
    }

    /**
     * 创建[催收工作台]流程记录
     *
     * @param orderId           主订单ID
     * @param collectionOrderId 催收单ID
     * @param processInstId     流程实例ID
     * @return
     */
    private Long create(Long orderId, Long collectionOrderId, String processInstId) {

        LoanProcessCollectionDO loanProcessCollectionDO = new LoanProcessCollectionDO();

        loanProcessCollectionDO.setOrderId(orderId);
        loanProcessCollectionDO.setCollectionOrderId(collectionOrderId);
        loanProcessCollectionDO.setProcessInstId(processInstId);

        loanProcessCollectionDO.setGmtCreate(new Date());
        loanProcessCollectionDO.setGmtModify(new Date());

        int count = loanProcessCollectionDOMapper.insertSelective(loanProcessCollectionDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return loanProcessCollectionDO.getId();
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
    private LoanProcessCollectionDO getLoanProcess(Long processId) {
        LoanProcessCollectionDO loanProcessDO = loanProcessCollectionDOMapper.selectByPrimaryKey(processId);
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
     * @param startTaskIdList      起始任务ID列表
     * @param processInstId
     * @param approval
     * @param currentLoanProcessDO
     */
    private void syncProcess(List<String> startTaskIdList, String processInstId, ApprovalParam approval,
                             LoanProcessCollectionDO currentLoanProcessDO) {

        // 更新状态
        LoanProcessCollectionDO loanProcessDO = new LoanProcessCollectionDO();
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

//        //【资料审核】打回到【业务申请】 标记
//        if (MATERIAL_REVIEW.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_REJECT_MANUAL.equals(approval.getAction())) {
//            loanProcessDO.setLoanApplyRejectOrginTask(MATERIAL_REVIEW.getCode());
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
    private void updateLoanProcess(LoanProcessCollectionDO loanProcessDO) {
        loanProcessDO.setGmtModify(new Date());
        int count = loanProcessCollectionDOMapper.updateByPrimaryKeySelective(loanProcessDO);
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
    private void updateCurrentTaskProcessStatus(LoanProcessCollectionDO loanProcessDO, String taskDefinitionKey,
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
        doUpdateCurrentTaskProcessStatus(loanProcessDO, taskDefinitionKey, taskProcessStatus);
    }

    /**
     * 执行 - 更新本地已执行的任务状态
     *
     * @param loanProcessDO
     * @param taskDefinitionKey
     * @param taskProcessStatus
     */
    private void doUpdateCurrentTaskProcessStatus(LoanProcessCollectionDO loanProcessDO,
                                                  String taskDefinitionKey, Byte taskProcessStatus) {
        // 方法名拼接   setXXX
        String methodBody = null;
        for (LoanProcessEnum e : LoanProcessEnum.values()) {

            if (e.getCode().equals(taskDefinitionKey)) {

                String[] keyArr = null;

                if (taskDefinitionKey.startsWith("servicetask")) {
                    keyArr = taskDefinitionKey.split("servicetask");
                } else if (taskDefinitionKey.startsWith("usertask")) {
                    keyArr = taskDefinitionKey.split("usertask");
                }

                // 下划线转驼峰
                methodBody = StringUtil.underline2Camel(keyArr[1]);
                break;
            }
        }

        // setXX
        String methodName = "set" + methodBody;

        // 反射执行
        try {

            // 获取反射对象
            Class<? extends LoanProcessCollectionDO> loanProcessDOClass = loanProcessDO.getClass();
            // 获取对应method
            Method method = loanProcessDOClass.getMethod(methodName, Byte.class);
            // 执行method
            Object result = method.invoke(loanProcessDO, taskProcessStatus);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
    private void updateNextTaskProcessStatus(LoanProcessCollectionDO loanProcessDO, String processInstanceId, List<String> startTaskIdList,
                                             ApprovalParam approval, LoanProcessCollectionDO currentLoanProcessDO) {

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
    private void doUpdateNextTaskProcessStatus(List<Task> nextTaskList, LoanProcessCollectionDO loanProcessDO,
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
     * @param loanProcessDO
     */
    private Map<String, Object> setAndGetVariables(ApprovalParam approval, LoanProcessCollectionDO loanProcessDO) {
        Map<String, Object> variables = Maps.newHashMap();

        // 流程变量：action
        variables.put(PROCESS_VARIABLE_ACTION, approval.getAction());

        fillOtherVariables(variables, approval, loanProcessDO);

        return variables;
    }


    /**
     * 填充其他的流程变量
     *
     * @param variables
     * @param approval
     * @param loanProcessDO
     */
    private void fillOtherVariables(Map<String, Object> variables, ApprovalParam approval, LoanProcessCollectionDO loanProcessDO) {

        // 【催收工作台】
        if (COLLECTION_WORKBENCH.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {

            // A  -> [上门拖车]
            if ("A".equals(approval.getChoice())) {

                variables.put(PROCESS_VARIABLE_TARGET, VISIT_COLLECTION_REVIEW.getCode());
            }

            // B  -> [法务审核]
            else if ("B".equals(approval.getChoice())) {

                variables.put(PROCESS_VARIABLE_TARGET, LEGAL_REVIEW.getCode());
            }

            // C  -> [上门拖车] + [法务审核]
            else if ("C".equals(approval.getChoice())) {

                Byte visitCollectionReviewStatus = loanProcessDO.getVisitCollectionReview();
                Byte legalReviewStatus = loanProcessDO.getLegalReview();

                // 第一次走
                if (TASK_PROCESS_INIT.equals(visitCollectionReviewStatus) && TASK_PROCESS_INIT.equals(legalReviewStatus)) {
                    variables.put(PROCESS_VARIABLE_TARGET, StringUtils.EMPTY);
                }

                // 第二+次走
                else if (!TASK_PROCESS_INIT.equals(visitCollectionReviewStatus)) {
                    variables.put(PROCESS_VARIABLE_TARGET, LEGAL_REVIEW.getCode());
                } else if (!TASK_PROCESS_INIT.equals(legalReviewStatus)) {
                    variables.put(PROCESS_VARIABLE_TARGET, VISIT_COLLECTION_REVIEW.getCode());
                }

            }
        }

        // [上门拖车]
        else if (VISIT_COLLECTION.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {

            if ("A".equals(approval.getChoice())) {
                variables.put(PROCESS_VARIABLE_TARGET, CAR_HANDLE.getCode());
            } else if ("B".equals(approval.getChoice())) {
                variables.put(PROCESS_VARIABLE_TARGET, SETTLE_ORDER.getCode());
            }

        }
    }
}
