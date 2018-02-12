package com.yunche.loan.service.impl;

import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.constant.ProcessActionEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.dataObj.InstLoanOrderDO;
import com.yunche.loan.domain.dataObj.InstProcessNodeDO;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.service.CustService;
import com.yunche.loan.service.LoanOrderService;
import com.yunche.loan.service.LoanProcessService;
import com.yunche.loan.service.ProcessNodeService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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
    private CustService custService;

    @Autowired
    private LoanOrderService loanOrderService;

    @Autowired
    private ProcessNodeService processNodeService;

    @Override
    public ResultBean<String> startProcessInstance(Long operatorId, String operatorName, String operatorRole) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("operatorId", operatorId);
        variables.put("operatorName", operatorName);
        variables.put("operatorRole", operatorRole);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dev_loan_process", variables);

        // 创建贷款订单
        InstLoanOrderDO instLoanOrderDO = loanOrderService.create(processInstance.getProcessInstanceId()).getData();

        // 记录流程执行节点
        InstProcessNodeDO instProcessNodeDO = new InstProcessNodeDO();
        instProcessNodeDO.setProcessInstId(processInstance.getProcessInstanceId());
        instProcessNodeDO.setOrderId(instLoanOrderDO.getOrderId());
        instProcessNodeDO.setNodeCode(LoanProcessEnum.START.getCode());
        instProcessNodeDO.setNodeName(LoanProcessEnum.START.getName());
        instProcessNodeDO.setPreviousNodeCode(null);
        instProcessNodeDO.setNextNodeCode(LoanProcessEnum.CREDIT_APPLY.getCode());
        instProcessNodeDO.setStatus(ProcessActionEnum.PASS.name());
        instProcessNodeDO.setOperatorId(operatorId);
        instProcessNodeDO.setOperatorName(operatorName);
        instProcessNodeDO.setOperatorRole(operatorRole);
        processNodeService.insert(instProcessNodeDO);

        return ResultBean.ofSuccess(processInstance.getProcessInstanceId(), "[" + LoanProcessEnum.START.getName() + "]成功");
    }

    @Override
    public ResultBean<Void> creditApply(InstLoanOrderVO instLoanOrderVO, String processId,
                                        Long operatorId, String operatorName, String operatorRole) {
        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();

        InstLoanOrderDO instLoanOrderDO = loanOrderService.getByProcInstId(processId).getData();
        instLoanOrderVO.setOrderId(instLoanOrderDO.getOrderId());

        // 记录流程执行节点
        InstProcessNodeDO instProcessNodeDO = new InstProcessNodeDO();
        instProcessNodeDO.setOrderId(instLoanOrderDO.getOrderId());
        instProcessNodeDO.setProcessInstId(processId);
        instProcessNodeDO.setNodeCode(LoanProcessEnum.CREDIT_APPLY.getCode());
        instProcessNodeDO.setNodeName(LoanProcessEnum.CREDIT_APPLY.getName());
        instProcessNodeDO.setPreviousNodeCode(LoanProcessEnum.START.getCode());
        instProcessNodeDO.setNextNodeCode(LoanProcessEnum.CREDIT_SAVE.getCode());
        instProcessNodeDO.setStatus(ProcessActionEnum.PASS.name());
//        Long operatorId = (Long) runtimeService.getVariable(task.getExecutionId(), "operatorId");
//        String operatorName = (String) runtimeService.getVariable(task.getExecutionId(), "operatorName");
//        String operatorRole = (String) runtimeService.getVariable(task.getExecutionId(), "operatorRole");
        instProcessNodeDO.setOperatorId(operatorId);
        instProcessNodeDO.setOperatorName(operatorName);
        instProcessNodeDO.setOperatorRole(operatorRole);
        processNodeService.insert(instProcessNodeDO);

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("custBaseInfoVO", instLoanOrderVO.getCustBaseInfoVO());
        taskVariables.put("instLoanOrderVO", instLoanOrderVO);
        taskVariables.put("amountGrade", instLoanOrderVO.getAmountGrade());
        taskVariables.put("processId", processId);
        taskService.complete(task.getId(), taskVariables);

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.CREDIT_APPLY.getName() + "]任务处理成功");
    }

    @Override
    public ResultBean<Void> creditVerify(String processId, String action,
                                         Long operatorId, String operatorName, String operatorRole) {
        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        InstLoanOrderVO instLoanOrderVO = (InstLoanOrderVO) runtimeService.getVariable(task.getExecutionId(), "instLoanOrderVO");

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("processAction", action);
        taskService.complete(task.getId(), taskVariables);

        // 记录流程执行节点
        InstProcessNodeDO instProcessNodeDO = new InstProcessNodeDO();

        instProcessNodeDO.setOrderId(instLoanOrderVO.getOrderId());
        instProcessNodeDO.setProcessInstId(processId);
        instProcessNodeDO.setNodeCode(LoanProcessEnum.CREDIT_VERIFY.getCode());
        instProcessNodeDO.setNodeName(LoanProcessEnum.CREDIT_VERIFY.getName());
        instProcessNodeDO.setPreviousNodeCode(LoanProcessEnum.CREDIT_SAVE.getCode());
        instProcessNodeDO.setNextNodeCode(LoanProcessEnum.BANK_CREDIT_RECORD.getCode() + "&" + LoanProcessEnum.SOCIAL_CREDIT_RECORD.getCode());
        instProcessNodeDO.setStatus(action);
        instProcessNodeDO.setOperatorId(operatorId);
        instProcessNodeDO.setOperatorName(operatorName);
        instProcessNodeDO.setOperatorRole(operatorRole);
        processNodeService.insert(instProcessNodeDO);

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.CREDIT_VERIFY.getName() + "]任务处理成功");
    }

    @Override
    public ResultBean<Void> bankCreditRecord(CustBaseInfoVO custBaseInfoVO, String processId, String action,
                                             Long operatorId, String operatorName, String operatorRole) {
        Task task = taskService.createTaskQuery().processInstanceId(processId).taskName(LoanProcessEnum.BANK_CREDIT_RECORD.getName()).singleResult();
        InstLoanOrderVO instLoanOrderVO = (InstLoanOrderVO) runtimeService.getVariable(task.getExecutionId(), "instLoanOrderVO");

        // 更新客户的银行征信数据
        custService.updateMainCust(custBaseInfoVO);

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("processAction", action);
        taskService.complete(task.getId(), taskVariables);

        // 记录流程执行节点
        InstProcessNodeDO instProcessNodeDO = new InstProcessNodeDO();
        instProcessNodeDO.setOrderId(instLoanOrderVO.getOrderId());
        instProcessNodeDO.setProcessInstId(processId);
        instProcessNodeDO.setNodeCode(LoanProcessEnum.BANK_CREDIT_RECORD.getCode());
        instProcessNodeDO.setNodeName(LoanProcessEnum.BANK_CREDIT_RECORD.getName());
        instProcessNodeDO.setPreviousNodeCode(LoanProcessEnum.CREDIT_VERIFY.getCode());
        instProcessNodeDO.setNextNodeCode(LoanProcessEnum.LOAN_APPROVE.getCode());
        instProcessNodeDO.setStatus(action);
        instProcessNodeDO.setOperatorId(operatorId);
        instProcessNodeDO.setOperatorName(operatorName);
        instProcessNodeDO.setOperatorRole(operatorRole);
        processNodeService.insert(instProcessNodeDO);

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.BANK_CREDIT_RECORD.getName() + "]任务处理成功");
    }

    @Override
    public ResultBean<Void> socialCreditRecord(CustBaseInfoVO custBaseInfoVO, String processId, String action,
                                               Long operatorId, String operatorName, String operatorRole) {
        Task task = taskService.createTaskQuery().processInstanceId(processId).taskName(LoanProcessEnum.SOCIAL_CREDIT_RECORD.getName()).singleResult();
        InstLoanOrderVO instLoanOrderVO = (InstLoanOrderVO) runtimeService.getVariable(task.getExecutionId(), "instLoanOrderVO");

        // 更新客户的社会征信数据
        custService.updateMainCust(custBaseInfoVO);

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("processAction", action);
        taskService.complete(task.getId(), taskVariables);

        // 记录流程执行节点
        InstProcessNodeDO instProcessNodeDO = new InstProcessNodeDO();
        instProcessNodeDO.setOrderId(instLoanOrderVO.getOrderId());
        instProcessNodeDO.setProcessInstId(processId);
        instProcessNodeDO.setNodeCode(LoanProcessEnum.SOCIAL_CREDIT_RECORD.getCode());
        instProcessNodeDO.setNodeName(LoanProcessEnum.SOCIAL_CREDIT_RECORD.getName());
        instProcessNodeDO.setPreviousNodeCode(LoanProcessEnum.CREDIT_VERIFY.getCode());
        instProcessNodeDO.setNextNodeCode(LoanProcessEnum.LOAN_APPROVE.getCode());
        instProcessNodeDO.setStatus(action);
        instProcessNodeDO.setOperatorId(operatorId);
        instProcessNodeDO.setOperatorName(operatorName);
        instProcessNodeDO.setOperatorRole(operatorRole);
        processNodeService.insert(instProcessNodeDO);

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.SOCIAL_CREDIT_RECORD.getName() + "]任务处理成功");
    }

    @Override
    public ResultBean<Void> loanApprove(InstLoanOrderVO instLoanOrderVO, String processId, Long operatorId, String operatorName, String operatorRole) {
        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();

        // 补充贷款业务单信息
        loanOrderService.update(instLoanOrderVO);
        // 补充客户信息
        custService.updateMainCust(instLoanOrderVO.getCustBaseInfoVO());

        // 记录流程执行节点
        InstProcessNodeDO instProcessNodeDO = new InstProcessNodeDO();
        instProcessNodeDO.setOrderId(instLoanOrderVO.getOrderId());
        instProcessNodeDO.setProcessInstId(processId);
        instProcessNodeDO.setNodeCode(LoanProcessEnum.LOAN_APPROVE.getCode());
        instProcessNodeDO.setNodeName(LoanProcessEnum.LOAN_APPROVE.getName());
        instProcessNodeDO.setPreviousNodeCode(LoanProcessEnum.BANK_CREDIT_RECORD.getCode());
        instProcessNodeDO.setNextNodeCode(LoanProcessEnum.TELEPHONE_VERIFY.getCode());
        instProcessNodeDO.setStatus(ProcessActionEnum.PASS.name());
        instProcessNodeDO.setOperatorId(operatorId);
        instProcessNodeDO.setOperatorName(operatorName);
        instProcessNodeDO.setOperatorRole(operatorRole);
        processNodeService.insert(instProcessNodeDO);

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("custBaseInfoVO", instLoanOrderVO.getCustBaseInfoVO());
        taskVariables.put("instLoanOrderVO", instLoanOrderVO);
        taskVariables.put("amountGrade", instLoanOrderVO.getAmountGrade());
        taskVariables.put("processId", processId);
        taskService.complete(task.getId(), taskVariables);

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.LOAN_APPROVE.getName() + "]任务处理成功");
    }

}
