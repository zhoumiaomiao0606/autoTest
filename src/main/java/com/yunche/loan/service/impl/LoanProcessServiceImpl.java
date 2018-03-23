package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.mapper.*;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_DONE;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_REJECT;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.*;
import static com.yunche.loan.config.constant.LoanProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.LoanUserGroupConst.*;

/**
 * Created by zhouguoliang on 2018/1/30.
 */
@Service
public class LoanProcessServiceImpl implements LoanProcessService {
    private static org.apache.log4j.Logger logger  = org.apache.log4j.Logger.getLogger(LoanProcessServiceImpl.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private UserGroupDOMapper userGroupDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private LoanProcessDOMapper loanProcessDOMapper;

    @Autowired
    private JpushService jpushService;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    /**
     * 根据当前节点ID获取下一个节点ID
     *
     * @param procInstanceId
     * @return
     */
    private String nextProcessInstId(String procInstanceId) {

        // 首先是根据流程ID获取当前任务
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstanceId).list();

        // 下一个节点ID
        final String[] nextId = {null};

        return nextId[0];
    }

    @Override
    @Transactional
    public ResultBean<Void> approval(ApprovalParam approval) {
        Preconditions.checkNotNull(approval.getOrderId(), "业务单号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(approval.getTaskDefinitionKey()), "执行任务不能为空");
        Preconditions.checkNotNull(approval.getAction(), "审核结果不能为空");
        if (ACTION_INFO_SUPPLEMENT.equals(approval.getAction())) {
            Preconditions.checkNotNull(approval.getSupplementType(), "资料增补类型不能为空");
        }

        // 业务单
        LoanOrderDO loanOrderDO = getLoanOrder(approval.getOrderId());

        // 获取任务
        Task task = getTask(loanOrderDO.getProcessInstId(), approval.getTaskDefinitionKey());

        // 流程变量
        Map<String, Object> variables = setAndGetVariables(task, approval, loanOrderDO.getLoanBaseInfoId());

        // 执行任务
        execTask(task, variables, approval, loanOrderDO);

        // 征信申请记录拦截
        execCreditRecordFilterTask(task, loanOrderDO.getProcessInstId(), approval.getTaskDefinitionKey(), approval.getAction(), variables);

        // 业务申请 & 上门调查 拦截
        execLoanApplyVisitVerifyFilterTask(task, loanOrderDO.getProcessInstId(), approval.getTaskDefinitionKey(), approval.getAction());

        // 更新任务状态
//        updateLoanOrderTaskDefinitionKey(approval.getOrderId(), approval.getTaskDefinitionKey());

        push(loanOrderDO,approval.getTaskDefinitionKey(),approval.getAction(),approval);

        return ResultBean.ofSuccess(null, "审核成功");
    }
    /**
     * 换行
     */
    public static String NEW_LINE = System.getProperty("line.separator");

    //推送
    private void push(LoanOrderDO loanOrderDO,String taskDefinitionKey,Integer action,ApprovalParam approval){
        Long baseInfoId = loanOrderDO.getLoanBaseInfoId();
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseInfoId);
        if(loanBaseInfoDO != null){
            StringBuffer cstr = new StringBuffer("客户:");
            StringBuffer bstr= new StringBuffer("您所提交的");
            StringBuffer msg = new StringBuffer("");
           LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(),new Byte("0"));
            String customerName = null;
            if(loanCustomerDO!=null){
                    customerName = loanCustomerDO.getName();
            }
            cstr.append("<"+customerName+">").append(LoanProcessEnum.getNameByCode(taskDefinitionKey));
            bstr.append(LoanProcessEnum.getNameByCode(taskDefinitionKey));
            //0 未知 1 正常 2 提示  3 错误 4 警告
            //* 审核结果：0-REJECT / 1-PASS / 2-CANCEL / 3-资料增补
            Byte type = new Byte("0");
            //贷款信息不为空时候才会进行push 不然不知道推给谁
            if(action.intValue() == 0){
                cstr.append("被打回");
                bstr.append("被打回");
                msg.append(approval.getInfo());
                type = new Byte("3");
            }

            if(action.intValue() == 1){
                cstr.append("审核通过");
                bstr.append("审核通过");
                msg.append(approval.getInfo());
                type = new Byte("1");
            }

            if(action.intValue() == 2){
                cstr.append("弃单");
                bstr.append("弃单");
                msg.append(approval.getInfo());
                type = new Byte("4");
            }

            if(action.intValue() == 3){
                cstr.append("需要资料增补");
                bstr.append("需要资料增补");
                msg.append("增补说明"+approval.getSupplementInfo()+NEW_LINE+"内容:"+approval.getSupplementContent());
                type = new Byte("2");
            }

            jpushService.push(loanBaseInfoDO.getSalesmanId(),loanOrderDO.getId(),cstr.toString(),bstr.toString(),msg.toString(),taskDefinitionKey,type);


        }


    }

    /**
     * 执行任务
     *
     * @param task
     * @param variables
     * @param approval
     * @param loanOrderDO
     */
    private void execTask(Task task, Map<String, Object> variables, ApprovalParam approval, LoanOrderDO loanOrderDO) {

        // 先获取提交之前的待执行任务列表
        List<Task> startTaskList = taskService.createTaskQuery()
                .processInstanceId(task.getProcessInstanceId())
                .list();

        if (TELEPHONE_VERIFY.getCode().equals(approval.getTaskDefinitionKey())) {
            // 执行电审任务
            execTelephoneVerifyTask(task, variables, approval, loanOrderDO.getId(), loanOrderDO.getLoanFinancialPlanId());
        } else {
            // 执行其他任务
            taskService.complete(task.getId(), variables);

            LoanProcessDO loanProcessDO = new LoanProcessDO();
            loanProcessDO.setOrderId(approval.getOrderId());

            // TODO 更新已执行的任务状态为：1-已提交
//            switch (approval.getTaskDefinitionKey()) {
////                case CREDIT_APPLY.getCode():
//                    System.out.println("");
//                    break;
//            }

//            loanProcessDO.setxxx(TASK_PROCESS_DONE);
        }

        // 再获取提交之后的待执行任务列表
        List<Task> endTaskList = taskService.createTaskQuery()
                .processInstanceId(task.getProcessInstanceId())
                .list();

        List<Task> newTaskList = Lists.newArrayList();
        List<Task> oldTaskList = Lists.newArrayList();
        List<Task> dealTaskList = Lists.newArrayList();

        startTaskList.parallelStream()
                .forEach(e -> {

                    if (endTaskList.contains(e)) {
                        // 仍然在：无变化
                        oldTaskList.add(e);
                    } else {
                        // 不存在：被执行了
                        dealTaskList.add(e);
                    }
                });

        // TODO 更新当前待执行的任务列表
//        updateLoanProcess(loanProcessDO);
    }

    /**
     * 获取业务单
     *
     * @param orderId
     * @return
     */
    private LoanOrderDO getLoanOrder(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");
        Preconditions.checkNotNull(loanOrderDO.getProcessInstId(), "流程实例ID不存在");
        return loanOrderDO;
    }

    /**
     * 执行电审任务
     *
     * @param task
     * @param variables
     * @param approval
     * @param orderId
     * @param loanFinancialPlanId
     */
    private void execTelephoneVerifyTask(Task task, Map<String, Object> variables, ApprovalParam approval, Long orderId, Long loanFinancialPlanId) {
        // 角色
        List<String> userGroupNameList = getUserGroupNameList();
        // 最大电审角色等级
        Byte maxRoleLevel = getTelephoneVerifyMaxRole(userGroupNameList);
        // 电审专员及以上有权电审
        Preconditions.checkArgument(null != maxRoleLevel && maxRoleLevel >= LEVEL_TELEPHONE_VERIFY_COMMISSIONER, "您无电审权限");

        // 是否已电审 及 电审员角色

        // 用对象记录操作日志      -- KEY加上roleLevel，放覆盖！！！
        String taskId = task.getId();
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        ApprovalInfoVO approvalInfoVO = new ApprovalInfoVO(loginUser.getId(), loginUser.getName(), approval.getAction(), approval.getInfo());
        String telephoneVerifyRoleLevelProcessKey = taskId + ":" + maxRoleLevel;
        variables.put(telephoneVerifyRoleLevelProcessKey, approvalInfoVO);

        // 如果是审核通过
        if (ACTION_PASS.equals(approval.getAction())) {

            // 获取贷款额度
            LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanFinancialPlanId);
            Preconditions.checkArgument(null != loanFinancialPlanDO && null != loanFinancialPlanDO.getLoanAmount(), "贷款额不能为空");
            double loanAmount = loanFinancialPlanDO.getLoanAmount().doubleValue();

            // 直接通过
            if (loanAmount >= 0 && loanAmount <= 100000) {
                // 全部角色直接过单
                passTelephoneVerifyTask(taskId, variables, orderId);
            } else if (loanAmount > 100000 && loanAmount <= 300000) {
                // 电审主管以上可过单
                if (maxRoleLevel < LEVEL_TELEPHONE_VERIFY_LEADER) {
                    // 记录
                    updateTelephoneVerify(orderId, maxRoleLevel);
                } else {
                    // 提交并记录
                    passTelephoneVerifyTask(taskId, variables, orderId);
                }

            } else if (loanAmount > 300000 && loanAmount <= 500000) {
                // 电审经理以上可过单
                if (maxRoleLevel < LEVEL_TELEPHONE_VERIFY_MANAGER) {
                    // 记录
                    updateTelephoneVerify(orderId, maxRoleLevel);
                } else {
                    // 提交并记录
                    passTelephoneVerifyTask(taskId, variables, orderId);
                }
            } else if (loanAmount > 500000) {
                // 总监以上可过单
                if (maxRoleLevel < LEVEL_DIRECTOR) {
                    // 记录
                    updateTelephoneVerify(orderId, maxRoleLevel);
                } else {
                    // 提交并记录
                    passTelephoneVerifyTask(taskId, variables, orderId);
                }
            }
        } else {
            // 否则，直接提交
            taskService.complete(task.getId(), variables);
        }
    }

    /**
     * 电审过单
     *
     * @param taskId
     * @param variables
     * @param orderId
     */
    private void passTelephoneVerifyTask(String taskId, Map<String, Object> variables, Long orderId) {
        // 提交
        taskService.complete(taskId, variables);
        // 更新电审流程
        updateTelephoneVerify(orderId, TASK_PROCESS_DONE);
    }

    /**
     * 更新电审流程
     *
     * @param orderId
     * @param telephoneVerifyProcess
     */
    private void updateTelephoneVerify(Long orderId, Byte telephoneVerifyProcess) {
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(orderId);
        loanProcessDO.setTelephoneVerify(telephoneVerifyProcess);

        updateLoanProcess(loanProcessDO);
    }

    /**
     * 获取改账号在【电审】中最大角色level
     *
     * @param userGroupNameList
     * @return
     */
    private Byte getTelephoneVerifyMaxRole(List<String> userGroupNameList) {
        if (CollectionUtils.isEmpty(userGroupNameList)) {
            return null;
        }

        final Byte[] maxLevel = {0};

        userGroupNameList.parallelStream()
                .filter(e -> StringUtils.isNotBlank(e))
                .forEach(e -> {

                    Byte level = TELEPHONE_VERIFY_USER_GROUP_LEVEL_MAP.get(e);
                    if (null != level) {
                        if (maxLevel[0] < level) {
                            maxLevel[0] = level;
                        }
                    }
                });

        return maxLevel[0];
    }

    /**
     * 获取用户组名称
     *
     * @return
     */
    public List<String> getUserGroupNameList() {
        // getUser
        EmployeeDO loginUser = SessionUtils.getLoginUser();

        // getUserGroup
        List<UserGroupDO> baseUserGroup = userGroupDOMapper.getBaseUserGroupByEmployeeId(loginUser.getId());

        // getUserGroupName
        List<String> userGroupNameList = null;
        if (!CollectionUtils.isEmpty(baseUserGroup)) {
            userGroupNameList = baseUserGroup.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {
                        return e.getName();
                    })
                    .collect(Collectors.toList());
        }
        return userGroupNameList;
    }

    /**
     * 业务申请 & 上门调查 拦截
     *
     * @param currentTask
     * @param processInstId
     * @param taskDefinitionKey
     * @param action
     */
    private void execLoanApplyVisitVerifyFilterTask(Task currentTask, String processInstId, String taskDefinitionKey, Integer action) {

        // 执行拦截任务
        if (isLoanApplyVisitVerifyFilterTask(taskDefinitionKey)) {
            // 获取所有正在执行的并行任务
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstId)
                    .list();

            // 上门调查：只有【提交】;  业务申请：只有【提交】&【弃单】;      -均无[打回]
            // PASS
            if (ACTION_PASS.equals(action)) {
                dealLoanApplyVisitVerifyFilterPassTask(currentTask, tasks);
            }
            // CANCEL
            else if (ACTION_CANCEL.equals(action)) {
                dealLoanApplyVisitVerifyFilterCancelTask(processInstId, tasks);
            }
        }
    }

    /**
     * 并行任务：-通过
     *
     * @param currentTask
     * @param tasks
     */
    private void dealLoanApplyVisitVerifyFilterPassTask(Task currentTask, List<Task> tasks) {
        // 是否都通过了
        if (!CollectionUtils.isEmpty(tasks)) {
            long count = tasks.parallelStream()
                    .filter(Objects::nonNull)
                    .filter(e -> !LOAN_APPLY_VISIT_VERIFY_FILTER.getCode().equals(e.getTaskDefinitionKey()))
                    .count();

            // 是 -> 放行
            if (count == 0) {

                // 仅保留一个子任务  当做 -> 主任务
                final Task[] mainTask = {null};

                // 其他子任务全部弃掉
                Map<String, Object> variables = Maps.newConcurrentMap();

                tasks.stream()
                        .filter(Objects::nonNull)
                        .forEach((Task task) -> {

                            if (currentTask.getExecutionId().equals(task.getExecutionId())) {
                                // 拿到当前子任务
                                mainTask[0] = task;
                            } else {
                                // 其他子任务 -> 弃单
                                variables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);
                                taskService.complete(task.getId(), variables);
                            }

                        });

                // "主任务"  ->  通过
                Task currentFilterTask = mainTask[0];
                if (null != currentFilterTask) {
                    variables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);
                    taskService.complete(currentFilterTask.getId(), variables);
                }
            }

            // 否 -> 等待  不做处理
        }
    }

    /**
     * 并行任务：-弃单
     *
     * @param processInstId
     * @param tasks
     */
    private void dealLoanApplyVisitVerifyFilterCancelTask(String processInstId, List<Task> tasks) {
        // 弃单 -> 将所有子任务弃单
        if (!CollectionUtils.isEmpty(tasks)) {

            // 全部弃掉
            if (!CollectionUtils.isEmpty(tasks)) {

                Map<String, Object> passVariables = Maps.newHashMap();
                Map<String, Object> cancelVariables = Maps.newHashMap();
                passVariables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);
                cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);

                tasks.stream()
                        .filter(Objects::nonNull)
                        .forEach(task -> {
                            if (!LOAN_APPLY_VISIT_VERIFY_FILTER.getCode().equals(task.getTaskDefinitionKey())) {
                                // 上门调查：未提交 -> 先提交
                                taskService.complete(task.getId(), passVariables);
                            } else {
                                // 上门调查 or 业务申请：已提交 -> 弃单
                                taskService.complete(task.getId(), cancelVariables);
                            }
                        });

                // 剩下子任务-业务申请： -> 弃单
                List<Task> taskList = taskService.createTaskQuery()
                        .processInstanceId(processInstId)
                        .list();
                if (!CollectionUtils.isEmpty(taskList)) {
                    // 刚提交 ->  再弃单
                    taskList.stream()
                            .filter(Objects::nonNull)
                            .forEach(task -> {
                                // 弃单
                                taskService.complete(task.getId(), cancelVariables);
                            });
                }
            }
        }
    }


    /**
     * 业务审核 & 资料审核 拦截
     *
     * @param currentTask
     * @param processInstId
     * @param taskDefinitionKey
     * @param action
     * @param variables
     */
    private void doBusinessMaterialReviewFilterTask(Task currentTask, String processInstId, String taskDefinitionKey, Integer action,
                                                    Map<String, Object> variables) {
        // 执行拦截任务
        if (isBusinessMaterialReviewFilterTask(taskDefinitionKey)) {
            // 获取所有正在执行的并行任务
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstId)
                    .list();

            // PASS
            if (ACTION_PASS.equals(action)) {
                dealPassTask1(currentTask, tasks);
            }
            // REJECT
            else if (ACTION_REJECT.equals(action)) {
                dealRejectTask2(currentTask, tasks);
            }
            // CANCEL
            else if (ACTION_CANCEL.equals(action)) {
                dealCancelTask3(processInstId, tasks);
            }
        }
    }

    /**
     * 征信申请记录拦截
     *
     * @param currentTask
     * @param processInstId
     * @param taskDefinitionKey
     * @param action
     * @param variables
     */
    private void execCreditRecordFilterTask(Task currentTask, String processInstId, String taskDefinitionKey, Integer action,
                                            Map<String, Object> variables) {

        // 执行拦截任务
        if (isBankAndSocialCreditRecordTask(variables, taskDefinitionKey)) {

            // 获取所有正在执行的并行任务
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstId)
                    .list();

            // PASS
            if (ACTION_PASS.equals(action)) {
                dealPassTask(currentTask, tasks);
            }
            // REJECT
            else if (ACTION_REJECT.equals(action)) {
                dealRejectTask(currentTask, tasks);
            }
            // CANCEL
            else if (ACTION_CANCEL.equals(action)) {
                dealCancelTask(tasks);
            }
        }
    }


    private void dealParallelTask(String processInstId, String taskDefinitionKey, Map<String, Object> transientVariables, Integer action, Long loanBaseInfoId) {
        // 银行&社会征信并行
        Integer loanAmount = (Integer) transientVariables.get("loanAmount");
        if (null == loanAmount) {
            // 贷款金额
            LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);
            Preconditions.checkNotNull(loanBaseInfoDO, "数据异常，贷款基本信息为空");
            Preconditions.checkNotNull(loanBaseInfoDO.getLoanAmount(), "数据异常，贷款金额为空");
            loanAmount = Integer.valueOf(loanBaseInfoDO.getLoanAmount());
        }

        boolean isBankAndSocialCreditRecordTask = BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey) || SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey)
                && null != loanAmount && loanAmount >= 2;
        if (isBankAndSocialCreditRecordTask) {

            // 任意一个子流程打回，则主流程打回，现有子任务全部结束掉
            boolean anyChildExecActionIsReject = ACTION_REJECT.equals(action);
            if (anyChildExecActionIsReject) {
                List<Task> tasks = taskService.createTaskQuery()
                        .processInstanceId(processInstId)
                        .orderByTaskCreateTime()
                        .desc()
                        .listPage(0, 3);

                if (!CollectionUtils.isEmpty(tasks)) {
                    List<String> taskIds = tasks.parallelStream()
                            .filter(Objects::nonNull)
                            .map(e -> {
                                if (BANK_CREDIT_RECORD.getCode().equals(e.getTaskDefinitionKey())
                                        || SOCIAL_CREDIT_RECORD.getCode().equals(e.getTaskDefinitionKey())) {
                                    return e.getId();
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(taskIds)) {
                        taskService.deleteTasks(taskIds, true);
                        taskService.deleteTasks(taskIds, "打回修改");


                        Task task = taskService.createTaskQuery()
                                .taskDefinitionKey(taskDefinitionKey)
                                .singleResult();

                        String executionId = task.getExecutionId();
//                        historyService.createHistoricTaskInstanceQuery()
//                                .executionId()
//
//
//                        taskService.resolveTask();
//
//                        taskService.delegateTask();
//
//                        taskService.getSubTasks();
//                        taskService.deleteTasks();

                    }
                }
            }

            // 任意一个子任务弃单，则主流程弃单
            boolean anyChildExecActionIsCancel = ACTION_CANCEL.equals(action);
            if (anyChildExecActionIsCancel) {
                runtimeService.deleteProcessInstance(processInstId, "弃单");
            }

            //  TODO 电审提交后，执行自动任务：servicetask_financial_scheme   -金融方案
//            if (TELEPHONE_VERIFY.getCode().equals(taskDefinitionKey)) {
//                taskService.complete(FINANCIAL_SCHEME.getCode());
//            }
        }
    }


    @Override
    public ResultBean<TaskStateVO> currentTask(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

//        List<Task> tasks = taskService.createTaskQuery()
//                .processInstanceId(loanOrderDO.getProcessInstId())
//                .list();
//
//        List<TaskStateVO> taskStateVOS = Lists.newArrayList();
//        if (!CollectionUtils.isEmpty(tasks)) {
//            taskStateVOS = tasks.parallelStream()
//                    .filter(Objects::nonNull)
//                    .map(task -> {
//
//                        TaskStateVO taskStateVO = new TaskStateVO();
//                        taskStateVO.setTaskDefinitionKey(task.getTaskDefinitionKey());
//                        taskStateVO.setTaskName(task.getName());
////                        taskStateVO.setTaskName(PROCESS_MAP.get(task.getTaskDefinitionKey()));
//
//                        return taskStateVO;
//                    })
//                    .collect(Collectors.toList());
//        }

        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(loanOrderDO.getProcessInstId())
                .orderByTaskCreateTime()
                .desc()
                .listPage(0, 1);

        Preconditions.checkArgument(!CollectionUtils.isEmpty(historicTaskInstanceList), "数据错误，任务记录不存在");

        HistoricTaskInstance lastHistoricTaskInstance = historicTaskInstanceList.get(0);
        TaskStateVO taskStateVO = new TaskStateVO();
        taskStateVO.setTaskDefinitionKey(lastHistoricTaskInstance.getTaskDefinitionKey());
        taskStateVO.setTaskName(lastHistoricTaskInstance.getName());
        // 任务状态
        Integer taskStatus = getTaskStatus(lastHistoricTaskInstance.getEndTime());
        taskStateVO.setTaskStatus(taskStatus);

        return ResultBean.ofSuccess(taskStateVO, "查询当前流程任务节点信息成功");
    }

    @Override
    public ResultBean<Integer> taskStatus(Long orderId, String taskDefinitionKey) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(taskDefinitionKey), "任务ID不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(loanOrderDO.getProcessInstId())
                .taskDefinitionKey(taskDefinitionKey)
                .orderByTaskCreateTime()
                .desc()
                .listPage(0, 1);

        // 任务状态
        Integer taskStatus = null;
        if (CollectionUtils.isEmpty(historicTaskInstanceList)) {
            taskStatus = TASK_NOT_REACH_CURRENT;
        } else {
            HistoricTaskInstance lastHistoricTaskInstance = historicTaskInstanceList.get(0);
            taskStatus = getTaskStatus(lastHistoricTaskInstance.getEndTime());
        }

        return ResultBean.ofSuccess(taskStatus, "当前流程任务节点状态");
    }

    /**
     * 任务状态
     *
     * @param endTime
     * @return
     */
    public Integer getTaskStatus(Date endTime) {
        Integer taskStatus = null;
        if (null != endTime) {
            // 已处理
            taskStatus = TASK_DONE;
        } else {
            // 未处理
            taskStatus = TASK_TODO;
        }
        return taskStatus;
    }

    /**
     * 执行 征信申请审核 或 银行&社会征信录入 任务时：填充流程变量-贷款金额
     *
     * @param variables
     * @param action
     * @param taskDefinitionKey
     * @param loanBaseInfoId
     * @param approvalInfoVO
     */
    private void fillLoanAmount(Map<String, Object> variables, Integer action, String taskDefinitionKey, Long loanBaseInfoId, ApprovalInfoVO approvalInfoVO) {
        // 征信申请审核且审核通过时
        boolean isApplyVerifyTaskAndActionIsPass = CREDIT_APPLY_VERIFY.getCode().equals(taskDefinitionKey) && ACTION_PASS.equals(action);
        // 银行&社会征信录入
        boolean isBankAndSocialCreditRecordTask = BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey);
        if (isApplyVerifyTaskAndActionIsPass || isBankAndSocialCreditRecordTask) {
            // 贷款金额
            LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);
            Preconditions.checkNotNull(loanBaseInfoDO, "数据异常，贷款基本信息为空");
            Preconditions.checkNotNull(loanBaseInfoDO.getLoanAmount(), "数据异常，贷款金额为空");
            // 日志记录
            approvalInfoVO.setLoanAmount(loanBaseInfoDO.getLoanAmount());
            // 流程变量
            variables.put(PROCESS_VARIABLE_LOAN_AMOUNT, loanBaseInfoDO.getLoanAmount());
        }
    }

    /**
     * 获取任务
     *
     * @param processInstId
     * @param taskDefinitionKey
     * @return
     */
    private Task getTask(String processInstId, String taskDefinitionKey) {
        // 获取当前流程taskId
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstId)
                .taskDefinitionKey(taskDefinitionKey)
                .singleResult();

        Preconditions.checkNotNull(task, "当前任务不存在");
        return task;
    }

    /**
     * TODO 设置并返回流程变量
     *
     * @param currentExecTask
     * @param approval
     * @param loanBaseInfoId
     */
    private Map<String, Object> setAndGetVariables(Task currentExecTask, ApprovalParam approval, Long loanBaseInfoId) {
        Map<String, Object> variables = Maps.newHashMap();

        // 流程变量
        variables.put(PROCESS_VARIABLE_ACTION, approval.getAction());

        // 操作日志：审核人ID、NAME、审核结果、审核备注
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        ApprovalInfoVO approvalInfoVO = new ApprovalInfoVO(loginUser.getId(), loginUser.getName(), approval.getAction(), approval.getInfo());
        variables.put(currentExecTask.getId(), approvalInfoVO);
//
//        // 前缀   KEY:ProcessInstId:ExecutionId:
//        String taskVariablePrefix = task.getTaskDefinitionKey() + ":" + task.getProcessInstanceId() + ":" + task.getExecutionId() + ":";
//
//        // 审核结果
//        variables.put(PROCESS_VARIABLE_ACTION, approval.getAction());
//        variables.put(taskVariablePrefix + PROCESS_VARIABLE_ACTION, approval.getAction());
//
//        // 审核备注
//        variables.put(taskVariablePrefix + PROCESS_VARIABLE_INFO, approval.getInfo());
//
//        // 审核人ID、NAM
//        variables.put(taskVariablePrefix + PROCESS_VARIABLE_USER_ID, loginUser.getId());
//        variables.put(taskVariablePrefix + PROCESS_VARIABLE_USER_NAME, loginUser.getName());

        // 添加流程变量-贷款金额
        fillLoanAmount(variables, approval.getAction(), currentExecTask.getTaskDefinitionKey(), loanBaseInfoId, approvalInfoVO);

        // 填充其他的流程变量
        fillOtherVariables(variables, approval, currentExecTask, approvalInfoVO);

        return variables;
    }

    /**
     * 填充其他的流程变量
     *
     * @param variables
     * @param approval
     * @param currentExecTask
     * @param approvalInfoVO
     */
    private void fillOtherVariables(Map<String, Object> variables, ApprovalParam approval, Task currentExecTask, ApprovalInfoVO approvalInfoVO) {
        // 资料增补
        if (ACTION_INFO_SUPPLEMENT.equals(approval.getAction())) {
            approvalInfoVO.setInfoSupplementType(approval.getSupplementType());
            approvalInfoVO.setInfoSupplementContent(approval.getSupplementContent());
            approvalInfoVO.setInfoSupplementInfo(approval.getSupplementInfo());
            approvalInfoVO.setInfoSupplementOriginTask(approval.getTaskDefinitionKey());
        }

        // TODO 【资料审核】打回到【业务申请】 标记
        else if (METERIAL_REVIEW.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_REJECT.equals(approval.getAction())) {
            // 记录 打回来源KEY、来源TaskId、去向KEY、去向TaskId
//            String originTaskKey = METERIAL_REVIEW.getCode();
//            String originTaskId = currentExecTask.getId();
//            String destTaskKey = LOAN_APPLY.getCode();
//
//            approvalInfoVO.setOriginTaskKey(originTaskKey);
//            approvalInfoVO.setOriginTaskId(originTaskId);
//            approvalInfoVO.setDestTaskKey(destTaskKey);

            // 更新本地审核流程记录
            LoanProcessDO loanProcessDO = new LoanProcessDO();
            loanProcessDO.setOrderId(approval.getOrderId());
            loanProcessDO.setLoanApply(TASK_PROCESS_REJECT);
            loanProcessDO.setLoanApplyRejectOrginTask(METERIAL_REVIEW.getCode());
            updateLoanProcess(loanProcessDO);
        }

        // 业务申请
        else if (LOAN_APPLY.getCode().equals(approval.getTaskDefinitionKey())) {

            // 是否 [打回] - 自于【资料审核】

            // 1.是否为打回
            LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(approval.getOrderId());
            Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

            Byte loanApply = loanProcessDO.getLoanApply();
            if (TASK_PROCESS_REJECT.equals(loanApply)) {

                // 2.打回自【资料审核】
                String loanApplyRejectOrginTask = loanProcessDO.getLoanApplyRejectOrginTask();
                if (METERIAL_REVIEW.getCode().equals(loanApplyRejectOrginTask)) {
                    // 添加流程变量 -打回来源 -> reject_origin_task
                    variables.put(PROCESS_VARIABLE_REJECT_ORIGIN_TASK, METERIAL_REVIEW.getCode());
                }
            }
        }

        // 电审
        else if (TELEPHONE_VERIFY.getCode().equals(approval.getTaskDefinitionKey())) {

            //


        }
    }

    /**
     * 更新本地流程记录
     *
     * @param loanProcessDO
     */
    private void updateLoanProcess(LoanProcessDO loanProcessDO) {
        int count = loanProcessDOMapper.updateByPrimaryKeySelective(loanProcessDO);
        loanProcessDO.setGmtModify(new Date());
        Preconditions.checkArgument(count > 0, "更新本地流程记录失败");
    }

    /**
     * 更新任务节点状态
     *
     * @param orderId
     * @param previousTaskDefKey
     */
    private void updateLoanOrderTaskDefinitionKey(Long orderId, String previousTaskDefKey) {
        // 获取最新任务节点
        String currentTaskDefinitionKey = null;
        ResultBean<TaskStateVO> currentTaskResultBean = currentTask(orderId);
        Preconditions.checkArgument(currentTaskResultBean.getSuccess(), currentTaskResultBean.getMsg());
        TaskStateVO taskStateVO = currentTaskResultBean.getData();
        if (null != taskStateVO) {
            currentTaskDefinitionKey = taskStateVO.getTaskDefinitionKey();
        }

        // update
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(orderId);
        loanOrderDO.setCurrentTaskDefKey(currentTaskDefinitionKey);
        loanOrderDO.setPreviousTaskDefKey(previousTaskDefKey);
        loanOrderDO.setGmtModify(new Date());
        int count = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        Preconditions.checkArgument(count > 0, "更新任务节点失败");
    }

    /**
     * 是否为：银行&社会征信并行任务
     *
     * @param variables
     * @param taskDefinitionKey
     * @return
     */
    public boolean isBankAndSocialCreditRecordTask(Map<String, Object> variables, String taskDefinitionKey) {
        Byte loanAmount = (Byte) variables.get("loanAmount");
        boolean isBankAndSocialCreditRecordTask = (BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey) || SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey))
                && null != loanAmount && loanAmount >= 2;
        return isBankAndSocialCreditRecordTask;
    }

    /**
     * 是否为：业务审核&资料审核 并行任务
     *
     * @param taskDefinitionKey
     * @return
     */
    private boolean isBusinessMaterialReviewFilterTask(String taskDefinitionKey) {
        boolean isBusinessMaterialReviewFilterTask = (BUSINESS_REVIEW.getCode().equals(taskDefinitionKey) || METERIAL_REVIEW.getCode().equals(taskDefinitionKey));
        return isBusinessMaterialReviewFilterTask;
    }

    /**
     * 是否为：业务申请&上门调查 并行任务
     *
     * @param taskDefinitionKey
     * @return
     */
    private boolean isLoanApplyVisitVerifyFilterTask(String taskDefinitionKey) {
        boolean isBusinessMaterialReviewFilterTask = (LOAN_APPLY.getCode().equals(taskDefinitionKey) || VISIT_VERIFY.getCode().equals(taskDefinitionKey));
        return isBusinessMaterialReviewFilterTask;
    }

    /**
     * 并行任务 -通过
     *
     * @param currentTask
     * @param tasks
     */
    private void dealPassTask1(Task currentTask, List<Task> tasks) {

        // 是否都通过了
        if (!CollectionUtils.isEmpty(tasks)) {
            long count = tasks.parallelStream()
                    .filter(Objects::nonNull)
                    .filter(e -> !BUSINESS_MATERIAL_REVIEW_FILTER.getCode().equals(e.getTaskDefinitionKey()))
                    .count();

            // 是 -> 放行
            if (count == 0) {

                // 仅保留一个子任务  当做 -> 主任务
                final Task[] mainTask = {null};

                // 其他子任务全部弃掉
                Map<String, Object> variables = Maps.newConcurrentMap();

                tasks.stream()
                        .filter(Objects::nonNull)
                        .forEach((Task task) -> {

                            if (currentTask.getExecutionId().equals(task.getExecutionId())) {
                                // 拿到当前子任务
                                mainTask[0] = task;
                            } else {
                                // 子任务 -> 弃单
                                variables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);
                                taskService.complete(task.getId(), variables);
                            }

                        });

                // "主任务"  ->  通过
                Task currentFilterTask = mainTask[0];
                if (null != currentFilterTask) {
                    variables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);
                    taskService.complete(currentFilterTask.getId(), variables);
                }
            }

            // 否 -> 等待  不做处理
        }
    }

    /**
     * 并行任务 -打回
     *
     * @param currentTask
     * @param tasks
     */
    private void dealRejectTask2(Task currentTask, List<Task> tasks) {
        // 打回 -> 结束掉其他子任务，然后打回
        if (!CollectionUtils.isEmpty(tasks)) {

            // 仅保留一个子任务  当做 -> 主任务
            final Task[] mainTask = {null};

            // 其他子任务全部  提交&弃掉
            if (!CollectionUtils.isEmpty(tasks)) {

                Map<String, Object> variables = Maps.newHashMap();
                Map<String, Object> cancelVariables = Maps.newHashMap();
                variables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);
                cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);

                tasks.stream()
                        .filter(Objects::nonNull)
                        .forEach((Task task) -> {

                            if (currentTask.getExecutionId().equals(task.getExecutionId())) {
                                // 拿到当前子任务
                                mainTask[0] = task;
                            } else {
                                // 子任务
                                if (!BUSINESS_MATERIAL_REVIEW_FILTER.getCode().equals(task.getTaskDefinitionKey())) {
                                    if (METERIAL_REVIEW.getCode().equals(task.getTaskDefinitionKey()) ||
                                            BUSINESS_REVIEW.getCode().equals(task.getTaskDefinitionKey())) {
                                        // action == 2 走拦截路线
                                        taskService.complete(task.getId(), cancelVariables);
                                    } else {
                                        // 未提交 -> 先提交
                                        taskService.complete(task.getId(), variables);
                                    }
                                } else {
                                    // 已提交 -> 弃单
                                    taskService.complete(task.getId(), cancelVariables);
                                }
                            }
                        });

                // "主任务"  ->  打回
                Task currentFilterTask = mainTask[0];
                if (null != currentFilterTask) {
                    variables.put(PROCESS_VARIABLE_ACTION, ACTION_REJECT);
                    taskService.complete(currentFilterTask.getId(), variables);
                }

                // 剩下子任务 -> 弃单
                List<Task> taskList = taskService.createTaskQuery()
                        .processInstanceId(currentTask.getProcessInstanceId())
                        .list();
                if (!CollectionUtils.isEmpty(taskList)) {
                    // 刚提交 ->  再弃单
                    taskList.stream()
                            .filter(Objects::nonNull)
                            .filter(task -> !currentFilterTask.getExecutionId().equals(task.getExecutionId()))
                            .forEach(task -> {
                                // 弃单
                                taskService.complete(task.getId(), cancelVariables);
                            });
                }
            }
        }
    }

    /**
     * 并行任务 -弃单
     *
     * @param processInstId
     * @param tasks
     */
    private void dealCancelTask3(String processInstId, List<Task> tasks) {

        // 弃单 -> 将所有子任务弃单
        if (!CollectionUtils.isEmpty(tasks)) {

            // 全部弃掉
            if (!CollectionUtils.isEmpty(tasks)) {

                Map<String, Object> passVariables = Maps.newHashMap();
                Map<String, Object> cancelVariables = Maps.newHashMap();
                passVariables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);
                cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);

                tasks.stream()
                        .filter(Objects::nonNull)
                        .forEach(task -> {
                            if (!BUSINESS_MATERIAL_REVIEW_FILTER.getCode().equals(task.getTaskDefinitionKey())) {
                                // 未提交 -> 先提交
                                if (METERIAL_REVIEW.getCode().equals(task.getTaskDefinitionKey()) ||
                                        BUSINESS_REVIEW.getCode().equals(task.getTaskDefinitionKey())) {
                                    // action == 2 走拦截路线
                                    taskService.complete(task.getId(), cancelVariables);
                                } else {
                                    // 未提交 -> 先提交
                                    taskService.complete(task.getId(), passVariables);
                                }
                            } else {
                                // 已提交 -> 弃单
                                taskService.complete(task.getId(), cancelVariables);
                            }
                        });


                // 剩下子任务 -> 弃单
                List<Task> taskList = taskService.createTaskQuery()
                        .processInstanceId(processInstId)
                        .list();
                if (!CollectionUtils.isEmpty(taskList)) {
                    // 刚提交 ->  再弃单
                    taskList.stream()
                            .filter(Objects::nonNull)
                            .forEach(task -> {
                                // 弃单
                                taskService.complete(task.getId(), cancelVariables);
                            });
                }
            }
        }
    }


    /**
     * 并行任务 -通过
     *
     * @param currentTask
     * @param tasks
     */

    private void dealPassTask(Task currentTask, List<Task> tasks) {

        // 是否都通过了
        if (!CollectionUtils.isEmpty(tasks)) {
            long count = tasks.parallelStream()
                    .filter(Objects::nonNull)
                    .filter(e -> !BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode().equals(e.getTaskDefinitionKey()))
                    .count();

            // 是 -> 放行
            if (count == 0) {

                // 仅保留一个子任务  当做 -> 主任务
                final Task[] onlyAsMainTask = {null};

                // 其他子任务全部弃掉

                Map<String, Object> cancelVariables = Maps.newConcurrentMap();

                tasks.stream()
                        .filter(Objects::nonNull)
                        .forEach((Task task) -> {

                            if (currentTask.getExecutionId().equals(task.getExecutionId())) {
                                // 拿到当前子任务
                                onlyAsMainTask[0] = task;

                            } else {

                                // 子任务 -> 弃单
                                cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);

                                taskService.complete(task.getId(), cancelVariables);

                                cancelVariables.clear();
                            }

                        });

                // "主任务"  ->  通过
                Task currentFilterTask = onlyAsMainTask[0];
                if (null != currentFilterTask) {
                    Map<String, Object> passVariables = Maps.newHashMap();
                    passVariables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);
                    taskService.complete(currentFilterTask.getId(), passVariables);
                }
            }

            // 否 -> 等待  不做处理
        }
    }

    /**
     * 并行任务 -打回
     *
     * @param currentTask
     * @param tasks
     */
    private void dealRejectTask(Task currentTask, List<Task> tasks) {

        // 打回 -> 结束掉其他子任务，然后打回
        if (!CollectionUtils.isEmpty(tasks)) {

            // 仅保留一个子任务  当做 -> 主任务
            final Task[] onlyAsMainTask = {null};

            // 其他子任务全部弃掉
            if (!CollectionUtils.isEmpty(tasks)) {

                Map<String, Object> cancelVariables = Maps.newConcurrentMap();

                tasks.stream()
                        .filter(Objects::nonNull)
                        .forEach((Task task) -> {

                            if (currentTask.getExecutionId().equals(task.getExecutionId())) {
                                // 拿到当前子任务
                                onlyAsMainTask[0] = task;

                            } else {
                                // 子任务 -> 弃单
                                cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);
                                taskService.complete(task.getId(), cancelVariables);
                                cancelVariables.clear();
                            }

                        });
            }

            // "主任务"  ->  打回
            Task currentFilterTask = onlyAsMainTask[0];
            if (null == currentFilterTask) {

            }
            Map<String, Object> rejectVariables = Maps.newHashMap();
            rejectVariables.put(PROCESS_VARIABLE_ACTION, ACTION_REJECT);
            taskService.complete(currentFilterTask.getId(), rejectVariables);
        }
    }

    /**
     * 并行任务 -弃单
     *
     * @param tasks
     */
    private void dealCancelTask(List<Task> tasks) {

        // 弃单 -> 将所有子任务弃单

        if (!CollectionUtils.isEmpty(tasks)) {

            // 全部弃掉
            if (!CollectionUtils.isEmpty(tasks)) {

                Map<String, Object> cancelVariables = Maps.newConcurrentMap();

                tasks.stream()
                        .filter(Objects::nonNull)
                        .forEach(task -> {

                            // 子任务 -> 弃单
                            cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);

                            taskService.complete(task.getId(), cancelVariables);

                            cancelVariables.clear();
                        });
            }
        }
    }
}
