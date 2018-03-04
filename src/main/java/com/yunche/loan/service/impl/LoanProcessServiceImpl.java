package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.LoanCustomerDOMapper;
import com.yunche.loan.dao.mapper.LoanProcessOrderDOMapper;
import com.yunche.loan.dao.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.viewObj.*;
import com.yunche.loan.service.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
//import org.activiti.engine.impl.pvm.PvmActivity;
//import org.activiti.engine.impl.pvm.PvmTransition;
//import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
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

import java.text.SimpleDateFormat;
import java.util.*;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.CustomerConst.*;
import static com.yunche.loan.config.constant.LoanProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.CREDIT_APPLY;

/**
 * Created by zhouguoliang on 2018/1/30.
 */
@Service
@Transactional
public class LoanProcessServiceImpl implements LoanProcessService {

    private static final Logger logger = LoggerFactory.getLogger(LoanProcessServiceImpl.class);


    @Autowired
    private LoanProcessOrderDOMapper loanProcessOrderDOMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;


    @Override
    public ResultBean<String> getOrderId() {
        String orderNum = createOrderNum();
        return ResultBean.ofSuccess(orderNum);
    }

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

//        tasks.stream()
//                .forEach(task -> {
//
//                    // 根据当前任务获取当前流程的流程定义
//                    ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
//
//                    // 根据流程定义获得所有的节点
//                    List<ActivityImpl> activitieList = processDefinitionEntity.getActivities();
//
//                    // 根据任务获取当前流程执行ID、执行实例以及当前流程节点的ID
//                    String excId = task.getExecutionId();
//                    ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId).singleResult();
//                    String activitiId = execution.getActivityId();
//
//                    // 然后循环activitiList
//                    // 并判断出当前流程所处节点，然后得到当前节点实例，根据节点实例获取所有从当前节点出发的路径，然后根据路径获得下一个节点实例：
//                    activitieList.stream()
//                            .filter(e -> activitiId.equals(e.getId()))
//                            .forEach(e -> {
//
////                                String id = e.getId();
////                                if (activitiId.equals(id)) {
//
//                                // 输出某个节点的某种属性
//                                logger.debug("当前任务：" + e.getProperty("name"));
//
//                                // 获取从某个节点出来的所有线路
//                                List<PvmTransition> outTransitions = e.getOutgoingTransitions();
//                                for (PvmTransition tr : outTransitions) {
//
//                                    // 获取线路的终点节点
//                                    PvmActivity ac = tr.getDestination();
//                                    logger.debug("下一步任务任务：" + ac.getProperty("name"));
//                                    nextId[0] = ac.getId();
//                                }
////                                }
//
//                            });
//
//                });
//
        return nextId[0];
    }


    @Override
    public ResultBean<Void> approval(ApprovalParam approval) {
        Preconditions.checkArgument(StringUtils.isNotBlank(approval.getId()), "业务单号不能为空");
        Preconditions.checkNotNull(approval.getAction(), "审核结果不能为空");

        // 业务单
        LoanProcessOrderDO loanProcessOrderDO = loanProcessOrderDOMapper.selectByPrimaryKey(approval.getId(), null);
        Preconditions.checkNotNull(loanProcessOrderDO, "业务单不存在");

        // 贷款金额
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanProcessOrderDO.getLoanBaseInfoId());
//        Preconditions.checkNotNull(loanBaseInfoDO, "贷款基本信息不能为空");
//        Preconditions.checkNotNull(loanBaseInfoDO.getLoanAmount(), "贷款金额不能为空");

        // 获取当前流程taskId
        Task task = taskService.createTaskQuery().processInstanceId(loanProcessOrderDO.getProcessInstId()).singleResult();
        Preconditions.checkNotNull(task, "业务单号有误或流程已完结");
        String taskId = task.getId();

        // 流程变量
        Map<String, Object> variables = Maps.newHashMap();
        // 审核结果
        variables.put("action", approval.getAction());
        // 审核备注
        variables.put("info", approval.getInfo());
        // 审核人ID
        Object principal = SecurityUtils.getSubject().getPrincipal();
        EmployeeDO user = new EmployeeDO();
        BeanUtils.copyProperties(principal, user);
        variables.put("userId", user.getId());
        // 贷款金额
        if (CREDIT_APPLY.getCode().equals(task.getTaskDefinitionKey())) {
            variables.put("loanAmount", Integer.valueOf(loanBaseInfoDO.getLoanAmount()));
        }

        // 执行任务
        taskService.complete(taskId, variables);

        // TODO 更新状态
//        loanProcessOrderDO.setStatus(action);
//        loanProcessOrderDO.setGmtModify(new Date());
//        int count = loanProcessOrderDOMapper.updateByPrimaryKeySelective(loanProcessOrderDO);
//        Preconditions.checkArgument(count > 0, "更新失败");

        return ResultBean.ofSuccess(null, "审核成功");
    }

    @Override
    public ResultBean<TaskStateVO> currentTask(String orderId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单号不能为空");

        LoanProcessOrderDO loanProcessOrderDO = loanProcessOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanProcessOrderDO, "业务单不存在");

        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(loanProcessOrderDO.getProcessInstId()).singleResult();
        TaskStateVO taskStateVO = new TaskStateVO();
        BeanUtils.copyProperties(historicTaskInstance, taskStateVO);
        // 任务状态
        Byte taskStatus = getTaskStatus(historicTaskInstance.getDeleteReason());
        taskStateVO.setTaskStatus(taskStatus);

        return ResultBean.ofSuccess(taskStateVO, "当前流程任务节点");
    }

    @Override
    public ResultBean<Byte> taskStatus(String orderId, String taskDefinitionKey) {
        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(taskDefinitionKey), "任务ID不能为空");

        LoanProcessOrderDO loanProcessOrderDO = loanProcessOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanProcessOrderDO, "业务单不存在");

        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(loanProcessOrderDO.getProcessInstId()).singleResult();
        // 任务状态
        Byte taskStatus = getTaskStatus(historicTaskInstance.getDeleteReason());

        return ResultBean.ofSuccess(taskStatus, "当前流程任务节点状态");
    }

    @Override
    public ResultBean<String> createCreditApply(CreditApplyVO creditApplyVO) {
        Preconditions.checkArgument(null != creditApplyVO.getLoanBaseInfo() || !custDetailIsEmpty(creditApplyVO.getCustDetail()),
                "贷款基本信息和客户信息不能都为空");

        // 生成业务单号
        String orderId = createOrderNum();

        // 开启流程实例
        startProcess(orderId);

        // 保存贷款基本信息
        createLoanBaseInfo(orderId, creditApplyVO.getLoanBaseInfo());

        // 保存客户信息
        createCustDetail(orderId, creditApplyVO.getCustDetail());

        return ResultBean.ofSuccess(orderId, "新建征信申请单成功");
    }

    @Override
    public ResultBean<Void> updateCreditApply(InstProcessOrderVO processInstOrder) {
        Preconditions.checkArgument(StringUtils.isNotBlank(processInstOrder.getId()), "业务单号不能为空");
        Preconditions.checkArgument(null != processInstOrder.getLoanBaseInfo() || !custDetailIsEmpty(processInstOrder.getCustDetail()),
                "贷款基本信息和客户信息不能都为空");

        // 编辑贷款基本信息
        updateOrInsertLoanBaseInfo(processInstOrder.getId(), processInstOrder.getLoanBaseInfo());

        // 编辑客户信息
        updateOrInsertCustDetail(processInstOrder.getId(), processInstOrder.getCustDetail());

        return ResultBean.ofSuccess(null, "编辑征信申请单成功");
    }


    /**
     * 编辑贷款基本信息
     *
     * @param orderId
     * @param loanBaseInfoVO
     */
    private void updateOrInsertLoanBaseInfo(String orderId, LoanBaseInfoVO loanBaseInfoVO) {
        if (null == loanBaseInfoVO) {
            return;
        }

        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        BeanUtils.copyProperties(loanBaseInfoVO, loanBaseInfoDO);
        loanBaseInfoDO.setGmtModify(new Date());

        if (null != loanBaseInfoVO.getId()) {
            // update
            loanBaseInfoDO.setGmtModify(new Date());
            int count = loanBaseInfoDOMapper.updateByPrimaryKeySelective(loanBaseInfoDO);
            Preconditions.checkArgument(count > 0, "编辑贷款基本信息失败");
        } else {
            // insert
            int count = loanBaseInfoDOMapper.insertSelective(loanBaseInfoDO);
            Preconditions.checkArgument(count > 0, "保存贷款基本信息失败");

            // 业务单关联贷款基本信息
            relaLoanBaseInfo(orderId, loanBaseInfoDO.getId());
        }
    }

    /**
     * 编辑客户信息
     *
     * @param orderId
     * @param custDetail
     */
    private void updateOrInsertCustDetail(String orderId, CustDetailVO custDetail) {
        if (null == custDetail) {
            return;
        }

        // 主贷人
        CustomerVO principalLenderVO = custDetail.getPrincipalLender();
        if (null != principalLenderVO) {
            updateOrInsertCustomer(orderId, principalLenderVO);
        }

        // 共贷人列表
        List<CustomerVO> commonLenderVOList = custDetail.getCommonLenderList();
        if (!CollectionUtils.isEmpty(commonLenderVOList)) {
            commonLenderVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(orderId, e);
                    });
        }

        // 担保人列表
        List<CustomerVO> guarantorVOList = custDetail.getGuarantorList();
        if (!CollectionUtils.isEmpty(guarantorVOList)) {
            guarantorVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(orderId, e);
                    });
        }

        // 紧急联系人列表
        List<CustomerVO> emergencyContactVOList = custDetail.getEmergencyContactList();
        if (!CollectionUtils.isEmpty(emergencyContactVOList)) {
            emergencyContactVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(orderId, e);
                    });
        }
    }

    /**
     * 编辑客户信息
     *
     * @param orderId
     * @param customerVO
     */
    private void updateOrInsertCustomer(String orderId, CustomerVO customerVO) {
        Preconditions.checkNotNull(customerVO.getCustType(), "客户类型不能为空");
        if (!CUST_TYPE_PRINCIPAL.equals(customerVO.getCustType())) {
            Preconditions.checkNotNull(customerVO.getPrincipalCustId(), "主贷人ID不能为空");
        }

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(customerVO, loanCustomerDO);
        loanCustomerDO.setFiles(JSON.toJSONString(customerVO.getFiles()));
        loanCustomerDO.setGmtModify(new Date());

        // update
        if (null != customerVO.getId()) {
            loanCustomerDO.setGmtModify(new Date());
            loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
        } else {
            // insert
            loanCustomerDO.setGmtCreate(new Date());
            loanCustomerDO.setGmtModify(new Date());
            loanCustomerDOMapper.insertSelective(loanCustomerDO);

            // 业务单关联主贷人
            if (CUST_TYPE_PRINCIPAL.equals(customerVO.getCustType())) {
                relaPrincipalLender(orderId, loanCustomerDO.getId());
            }
        }
    }


    /**
     * 开启流程实例
     *
     * @param orderId
     */

    private void startProcess(String orderId) {
        // 开启activiti流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("loan_process");

        // 开启本地业务流程单
        LoanProcessOrderDO loanProcessOrderDO = new LoanProcessOrderDO();
        loanProcessOrderDO.setProcessInstId(processInstance.getProcessInstanceId());
        loanProcessOrderDO.setId(orderId);
        loanProcessOrderDO.setGmtCreate(new Date());
        loanProcessOrderDO.setGmtModify(new Date());
        int count = loanProcessOrderDOMapper.insertSelective(loanProcessOrderDO);
        Preconditions.checkArgument(count > 0, "新建业务单失败");
    }

    /**
     * 创建客户信息
     *
     * @param orderId
     * @param custDetail
     */
    private void createCustDetail(String orderId, CustDetailVO custDetail) {
        // 空校验
        if (custDetailIsEmpty(custDetail)) {
            return;
        }

        // 主贷人
        CustomerVO principalLender = custDetail.getPrincipalLender();
        Preconditions.checkNotNull(principalLender, "主贷人信息不能为空");
        // 创建主贷人
        createCustomer(orderId, principalLender);
        // 主贷人ID
        Long principalCustId = principalLender.getId();

        // 共贷人列表
        List<CustomerVO> commonLenderList = custDetail.getCommonLenderList();
        if (!CollectionUtils.isEmpty(commonLenderList)) {
            commonLenderList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        e.setPrincipalCustId(principalCustId);
                        createCustomer(orderId, e);
                    });
        }

        // 担保人列表
        List<CustomerVO> guarantorList = custDetail.getGuarantorList();
        if (!CollectionUtils.isEmpty(guarantorList)) {
            guarantorList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        e.setPrincipalCustId(principalCustId);
                        createCustomer(orderId, e);
                    });
        }

        // 紧急联系人列表
        List<CustomerVO> emergencyContactList = custDetail.getEmergencyContactList();
        if (!CollectionUtils.isEmpty(emergencyContactList)) {
            emergencyContactList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        e.setPrincipalCustId(principalCustId);
                        createCustomer(orderId, e);
                    });
        }
    }

    /**
     * 校验客户信息是否均为空
     *
     * @param custDetail
     * @return
     */
    private boolean custDetailIsEmpty(CustDetailVO custDetail) {
        if (null == custDetail) {
            return true;
        }

        CustomerVO principalLender = custDetail.getPrincipalLender();
        List<CustomerVO> commonLenderList = custDetail.getCommonLenderList();
        List<CustomerVO> guarantorList = custDetail.getGuarantorList();
        List<CustomerVO> emergencyContactList = custDetail.getEmergencyContactList();

        // 客户信息是否均为空
        boolean custDetailIsEmpty = (null == principalLender) && CollectionUtils.isEmpty(commonLenderList)
                && CollectionUtils.isEmpty(guarantorList) && CollectionUtils.isEmpty(emergencyContactList);
        if (custDetailIsEmpty) {
            return true;
        }

        return false;
    }

    /**
     * 创建客户信息
     *
     * @param orderId
     * @param customerVO
     */
    private void createCustomer(String orderId, CustomerVO customerVO) {
        Preconditions.checkNotNull(customerVO, "客户信息不能为空");
        Preconditions.checkNotNull(customerVO.getCustType(), "客户类型不能为空");
        if (!CUST_TYPE_PRINCIPAL.equals(customerVO.getCustType())) {
            Preconditions.checkNotNull(customerVO.getPrincipalCustId(), "主贷人ID不能为空");
        }

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(customerVO, loanCustomerDO);
        loanCustomerDO.setFiles(JSON.toJSONString(customerVO.getFiles()));
        loanCustomerDO.setGmtCreate(new Date());
        loanCustomerDO.setGmtModify(new Date());
        int count = loanCustomerDOMapper.insertSelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "创建客户信息失败");

        // 业务单关联主贷人
        if (CUST_TYPE_PRINCIPAL.equals(customerVO.getCustType())) {
            // 关联
            relaPrincipalLender(orderId, loanCustomerDO.getId());
            // 用VO传递主贷人ID
            customerVO.setId(loanCustomerDO.getId());
        }
    }

    /**
     * 业务单关联主贷人
     *
     * @param orderId
     * @param principalId
     */
    private void relaPrincipalLender(String orderId, Long principalId) {
        LoanProcessOrderDO loanProcessOrderDO = new LoanProcessOrderDO();
        loanProcessOrderDO.setId(orderId);
        loanProcessOrderDO.setLoanCustomerId(principalId);
        loanProcessOrderDO.setGmtModify(new Date());
        int count = loanProcessOrderDOMapper.updateByPrimaryKeySelective(loanProcessOrderDO);
        Preconditions.checkArgument(count > 0, "关联主贷人失败");
    }

    /**
     * 贷款基本信息
     *
     * @param orderId
     * @param loanBaseInfoVO
     */
    private void createLoanBaseInfo(String orderId, LoanBaseInfoVO loanBaseInfoVO) {
        if (null == loanBaseInfoVO) {
            return;
        }

        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单ID不能为空");
        Preconditions.checkArgument(null != loanBaseInfoVO && null != loanBaseInfoVO.getPartnerId(), "合伙人不能为空");
        Preconditions.checkNotNull(loanBaseInfoVO.getSalesmanId(), "业务员不能为空");
        Preconditions.checkNotNull(loanBaseInfoVO.getAreaId(), "业务区域不能为空");
        Preconditions.checkNotNull(loanBaseInfoVO.getCarType(), "车辆类型不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(loanBaseInfoVO.getBank()), "贷款银行不能为空");
        Preconditions.checkNotNull(loanBaseInfoVO.getLoanAmount(), "预计贷款金额不能为空");

        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        BeanUtils.copyProperties(loanBaseInfoVO, loanBaseInfoDO);
        // 贷款基本信息
        loanBaseInfoDO.setStatus(VALID_STATUS);
        loanBaseInfoDO.setGmtCreate(new Date());
        loanBaseInfoDO.setGmtModify(new Date());
        int count = loanBaseInfoDOMapper.insertSelective(loanBaseInfoDO);
        Preconditions.checkArgument(count > 0, "保存贷款基本信息失败");

        // 业务单关联贷款基本信息
        relaLoanBaseInfo(orderId, loanBaseInfoDO.getId());
    }

    /**
     * 业务单关联贷款基本信息
     *
     * @param orderId
     * @param loanBaseInfoId
     */
    private void relaLoanBaseInfo(String orderId, Long loanBaseInfoId) {
        LoanProcessOrderDO loanProcessOrderDO = new LoanProcessOrderDO();
        loanProcessOrderDO.setId(orderId);
        loanProcessOrderDO.setLoanBaseInfoId(loanBaseInfoId);
        loanProcessOrderDO.setGmtModify(new Date());
        int count = loanProcessOrderDOMapper.updateByPrimaryKeySelective(loanProcessOrderDO);
        Preconditions.checkArgument(count > 0, "关联贷款基本信息失败");
    }


    /**
     * 生成业务单ID
     *
     * @return
     */
    private String createOrderNum() {
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        // new Date()为获取当前系统时间，也可使用当前时间戳
        String orderNum = "YC" + df.format(new Date());

        Random rm = new Random();
        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, 6);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        fixLenthString = fixLenthString.substring(1, 7);
        orderNum = orderNum + fixLenthString;

        return orderNum;
    }

    /**
     * 任务状态
     *
     * @param deleteReason
     * @return
     */
    public Byte getTaskStatus(String deleteReason) {
        Byte taskStatus = null;
        if (DELETE_RELEASE_HAS_DONE.equals(deleteReason)) {
            // 已处理
            taskStatus = TASK_DONE;
        } else if (StringUtils.isBlank(deleteReason)) {
            // 未处理
            taskStatus = TASK_TODO;
        }
        return taskStatus;
    }
}
