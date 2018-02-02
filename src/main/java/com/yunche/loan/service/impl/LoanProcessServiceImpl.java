package com.yunche.loan.service.impl;

import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.constant.ProcessActionEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.InstLoanOrderDOMapper;
import com.yunche.loan.dao.mapper.InstProcessNodeDOMapper;
import com.yunche.loan.domain.dataObj.InstLoanOrderDO;
import com.yunche.loan.domain.dataObj.InstProcessNodeDO;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.service.LoanProcessService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouguoliang on 2018/1/30.
 */
@Service
@Transactional
public class LoanProcessServiceImpl implements LoanProcessService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private InstLoanOrderDOMapper instLoanOrderDOMapper;

    @Autowired
    private InstProcessNodeDOMapper instProcessNodeDOMapper;

    @Override
    public ResultBean<String> startProcessInstance(Long operatorId, String operatorName, String operatorRole) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("operatorId", operatorId);
        variables.put("operatorName", operatorName);
        variables.put("operatorRole", operatorRole);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dev_loan_process", variables);

        return ResultBean.ofSuccess(processInstance.getProcessInstanceId(), "[" + LoanProcessEnum.START.getName() + "]成功");
    }

    @Override
    public ResultBean<Void> creditApply(InstLoanOrderVO instLoanOrderVO, String processId) {
        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();

        // 创建贷款订单
        InstLoanOrderDO instLoanOrderDO = new InstLoanOrderDO();
        BeanUtils.copyProperties(instLoanOrderVO, instLoanOrderDO);
        instLoanOrderDO.setStatus(0);
        instLoanOrderDOMapper.insert(instLoanOrderDO);
        // 记录流程执行节点
        InstProcessNodeDO instProcessNodeDO = new InstProcessNodeDO();
        instProcessNodeDO.setOrderId(instLoanOrderDO.getOrderId());
        instProcessNodeDO.setNodeCode(LoanProcessEnum.CREDIT_APPLY.getCode());
        instProcessNodeDO.setPreviousNodeCode(null);
        instProcessNodeDO.setNextNodeCode(LoanProcessEnum.CREDIT_SAVE.getCode());
        instProcessNodeDO.setStatus(0);
        Long operatorId = (Long) runtimeService.getVariable(task.getExecutionId(), "operatorId");
        String operatorName = (String) runtimeService.getVariable(task.getExecutionId(), "operatorName");
        String operatorRole = (String) runtimeService.getVariable(task.getExecutionId(), "operatorRole");
        instProcessNodeDO.setOperatorId(operatorId);
        instProcessNodeDO.setOperatorName(operatorName);
        instProcessNodeDO.setOperatorRole(operatorRole);
        instProcessNodeDOMapper.insert(instProcessNodeDO);

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("custBaseInfoVO", instLoanOrderVO.getCustBaseInfoVO());
        taskVariables.put("instLoanOrderDO", instLoanOrderDO);
        taskVariables.put("amountGrade", instLoanOrderDO.getAmountGrade());
        taskService.complete(task.getId(), taskVariables);

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.CREDIT_APPLY.getName() + "]任务处理成功");
    }

    @Override
    public ResultBean<Void> creditVerify(String processId, String action) {
        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("processAction", action);
        taskService.complete(task.getId(), taskVariables);

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.CREDIT_VERIFY.getName() + "]任务处理成功");
    }

    @Override
    public ResultBean<List<Task>> getTasks(String userGroupName) {
        List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(userGroupName).list();
        return ResultBean.ofSuccess(taskList);
    }

    @Override
    public ResultBean<Void> completeTasks(boolean isApproved, String taskId) {
        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("isApproved", isApproved);
        taskService.complete(taskId, taskVariables);

        return ResultBean.ofSuccess(null, "任务处理成功");
    }
}
