package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.BANK_CREDIT_RECORD;
import static com.yunche.loan.config.constant.LoanProcessEnum.CREDIT_APPLY_VERIFY;

/**
 * Created by zhouguoliang on 2018/1/30.
 */
@Service
@Transactional
public class LoanProcessServiceImpl implements LoanProcessService {

    private static final Logger logger = LoggerFactory.getLogger(LoanProcessServiceImpl.class);


    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;


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
    public ResultBean<Void> approval(ApprovalParam approval) {
        Preconditions.checkNotNull(approval.getOrderId(), "业务单号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(approval.getTaskDefinitionKey()), "执行任务不能为空");
        Preconditions.checkNotNull(approval.getAction(), "审核结果不能为空");

        // 业务单
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(approval.getOrderId(), null);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");
        Preconditions.checkNotNull(loanOrderDO.getProcessInstId(), "流程实例ID不存在");

        // 获取当前流程taskId
        Task task = taskService.createTaskQuery()
                .processInstanceId(loanOrderDO.getProcessInstId())
                .taskDefinitionKey(approval.getTaskDefinitionKey())
                .singleResult();
        Preconditions.checkNotNull(task, "当前任务不存在");
        String taskId = task.getId();

        // 流程变量
        String taskVariablePrefix = task.getTaskDefinitionKey() + ":" + task.getProcessInstanceId() + ":" + task.getExecutionId() + ":";
        Map<String, Object> variables = Maps.newHashMap();
        Map<String, Object> transientVariables = Maps.newHashMap();
        // 审核结果
        transientVariables.put("action", approval.getAction());
        variables.put(taskVariablePrefix + "action", approval.getAction());
        // 审核备注
        variables.put(taskVariablePrefix + "info", approval.getInfo());
        // 审核人ID
        Object principal = SecurityUtils.getSubject().getPrincipal();
        EmployeeDO user = new EmployeeDO();
        BeanUtils.copyProperties(principal, user);
        variables.put(taskVariablePrefix + "userId", user.getId());
        variables.put(taskVariablePrefix + "userName", user.getName());

        // 添加流程变量-贷款金额
        fillLoanAmount(transientVariables, approval.getAction(), task.getTaskDefinitionKey(), loanOrderDO.getLoanBaseInfoId());

        // 执行任务
        taskService.complete(taskId, variables, transientVariables);

        // TODO 更新状态
        loanOrderDO.setCurrentTaskDefKey(approval.getTaskDefinitionKey());
        loanOrderDO.setGmtModify(new Date());
        int count = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        Preconditions.checkArgument(count > 0, "更新失败");

        return ResultBean.ofSuccess(null, "审核成功");
    }


    @Override
    public ResultBean<TaskStateVO> currentTask(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

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
        Byte taskStatus = getTaskStatus(lastHistoricTaskInstance.getEndTime());
        taskStateVO.setTaskStatus(taskStatus);

        return ResultBean.ofSuccess(taskStateVO, "查询当前流程任务节点信息成功");
    }

    @Override
    public ResultBean<Byte> taskStatus(Long orderId, String taskDefinitionKey) {
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
        Byte taskStatus = null;
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
    public Byte getTaskStatus(Date endTime) {
        Byte taskStatus = null;
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
     * @param transientVariables
     * @param action
     * @param taskDefinitionKey
     * @param loanBaseInfoId
     */
    private void fillLoanAmount(Map<String, Object> transientVariables, Integer action, String taskDefinitionKey, Long loanBaseInfoId) {
        // 征信申请审核且审核通过时
        boolean isApplyVerifyTaskAndActionIsPass = CREDIT_APPLY_VERIFY.getCode().equals(taskDefinitionKey) && PASS.byteValue() == action.byteValue();
        // 银行&社会征信录入
        boolean isBankAndSocialCreditRecordTask = BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey);
        if (isApplyVerifyTaskAndActionIsPass || isBankAndSocialCreditRecordTask) {
            // 贷款金额
            LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);
            Preconditions.checkNotNull(loanBaseInfoDO, "数据异常，贷款基本信息为空");
            Preconditions.checkNotNull(loanBaseInfoDO.getLoanAmount(), "数据异常，贷款金额为空");
            transientVariables.put("loanAmount", Integer.valueOf(loanBaseInfoDO.getLoanAmount()));
        }
    }
}
