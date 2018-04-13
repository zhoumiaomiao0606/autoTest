package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yunche.loan.config.constant.LoanOrderProcessConst;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.mapper.*;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.*;
import org.activiti.engine.*;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import sun.misc.ObjectInputFilter;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.*;
import static com.yunche.loan.config.constant.LoanProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.LoanUserGroupConst.*;

/**
 * Created by zhouguoliang on 2018/1/30.
 */
@Service
public class LoanProcessServiceImpl implements LoanProcessService {

    /**
     * 换行符
     */
    public static final String NEW_LINE = System.getProperty("line.separator");

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

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
    private LoanInfoSupplementDOMapper loanInfoSupplementDOMapper;

    @Autowired
    private LoanProcessLogDOMapper loanProcessLogDOMapper;

    @Autowired
    private JpushService jpushService;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private PermissionService permissionService;


    @Override
    @Transactional
    public ResultBean<Void> approval(ApprovalParam approval) {
        Preconditions.checkNotNull(approval.getOrderId(), "业务单号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(approval.getTaskDefinitionKey()), "执行任务不能为空");
        Preconditions.checkNotNull(approval.getAction(), "审核结果不能为空");

        // 节点权限校验
        permissionService.checkTaskPermission(approval.getTaskDefinitionKey_());

        // 校验审核前提条件
        checkPreCondition(approval.getTaskDefinitionKey(), approval.getOrderId());

        // 业务单
        LoanOrderDO loanOrderDO = getLoanOrder(approval.getOrderId());

        // 日志
        log(approval);

        // 征信增补
        execCreditSupplementTask(approval, loanOrderDO.getProcessInstId());

        // 【发起/提交】资料增补单
        if (isStartOrEndInfoSupplement(approval)) {
            return execInfoSupplementTask(approval);
        }

        // 获取任务
        Task task = getTask(loanOrderDO.getProcessInstId(), approval.getTaskDefinitionKey());

        // 流程变量
        Map<String, Object> variables = setAndGetVariables(task, approval, loanOrderDO.getLoanBaseInfoId());

        // 执行任务
        execTask(task, variables, approval, loanOrderDO);

        // 推送
        push(loanOrderDO, approval.getTaskDefinitionKey(), approval.getAction(), approval);

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.getNameByCode(approval.getTaskDefinitionKey_()) + "]任务执行成功");
    }

    /**
     * 校验审核前提条件
     *
     * @param taskDefinitionKey
     * @param orderId
     */
    private void checkPreCondition(String taskDefinitionKey, Long orderId) {
        if (MATERIAL_REVIEW.getCode().equals(taskDefinitionKey)) {
            // 提车资料必须已经提交了
            LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
            Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");
            Preconditions.checkArgument(TASK_PROCESS_DONE.equals(loanProcessDO.getVehicleInformation()), "请先录入提车资料");
        }


        if (LOAN_APPLY.getCode().equals(taskDefinitionKey)) {
            // 客户资料、车辆信息、金融方案  必须均已录入
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, VALID_STATUS);
            Preconditions.checkNotNull(loanOrderDO, "订单不存在");

            Preconditions.checkNotNull(loanOrderDO.getLoanCustomerId(), "请先录入客户信息");
            Preconditions.checkNotNull(loanOrderDO.getLoanCarInfoId(), "请先录入车辆信息");
            Preconditions.checkNotNull(loanOrderDO.getLoanFinancialPlanId(), "请先录入金融方案");
        }
    }

    /**
     * 流程操作日志记录
     *
     * @param approval
     */
    private void log(ApprovalParam approval) {
        // 是否需要日志记录
        if (!approval.isNeedLog()) {
            return;
        }

        LoanProcessLogDO loanProcessLogDO = new LoanProcessLogDO();
        BeanUtils.copyProperties(approval, loanProcessLogDO);

        EmployeeDO loginUser = SessionUtils.getLoginUser();
        loanProcessLogDO.setUserId(loginUser.getId());
        loanProcessLogDO.setUserName(loginUser.getName());

        loanProcessLogDO.setCreateTime(new Date());

        int count = loanProcessLogDOMapper.insertSelective(loanProcessLogDO);
        Preconditions.checkArgument(count > 0, "操作日志记录失败");
    }

    /**
     * 征信增补
     *
     * @param approval
     * @param processInstanceId
     */
    private void execCreditSupplementTask(ApprovalParam approval, String processInstanceId) {
        // 是否【征信增补】
        boolean isCreditSupplementTask = CREDIT_SUPPLEMENT.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction());
        if (!isCreditSupplementTask) {
            return;
        }

        approval.setTaskDefinitionKey_(approval.getTaskDefinitionKey());

        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(approval.getOrderId());
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

        // 判断当前任务流程   是否在电审前
        Preconditions.checkArgument(TASK_PROCESS_INIT.equals(loanProcessDO.getTelephoneVerify()), "流程已过电审环节，无法发起征信增补");

        // 当前所有task
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();

        Preconditions.checkArgument(!CollectionUtils.isEmpty(tasks), "无可执行任务");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(approval.getOrderId(), null);
        Preconditions.checkNotNull(loanOrderDO, "订单不存在");

        approval.setAction(ACTION_REJECT_AUTO);

        tasks.stream()
                .filter(Objects::nonNull)
                .forEach(task -> {

                    String taskDefinitionKey = task.getTaskDefinitionKey();

                    boolean isBankAndSocialCreditRecordTask = BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey)
                            || SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey);

                    boolean isLoanApplyVisitVerifyFilterTask = (LOAN_APPLY.getCode().equals(taskDefinitionKey)
                            || VISIT_VERIFY.getCode().equals(taskDefinitionKey))
                            && !MATERIAL_REVIEW.getCode().equals(loanProcessDO.getLoanApplyRejectOrginTask());

                    if (isBankAndSocialCreditRecordTask || isLoanApplyVisitVerifyFilterTask) {
                        approval.setTaskDefinitionKey(task.getTaskDefinitionKey());
                    }

                });
    }

    /**
     * 是否为：【发起/提交】资料增补单
     *
     * @param approval
     * @return
     */
    private boolean isStartOrEndInfoSupplement(ApprovalParam approval) {
        // 发起资料增补
        boolean isStartInfoSupplement = ACTION_INFO_SUPPLEMENT.equals(approval.getAction());
        // 处理资料增补单（暂时-只有PASS操作 -即：END）
        boolean isEndInfoSupplement = INFO_SUPPLEMENT.getCode().equals(approval.getTaskDefinitionKey());
        boolean isStartOrEndInfoSupplement = isStartInfoSupplement || isEndInfoSupplement;
        return isStartOrEndInfoSupplement;
    }

    /**
     * 执行资料增补任务
     *
     * @param approval
     * @return
     */
    private ResultBean<Void> execInfoSupplementTask(ApprovalParam approval) {

        // 【发起】资料增补单
        if (ACTION_INFO_SUPPLEMENT.equals(approval.getAction())) {
            // 创建增补单
            startInfoSupplement(approval);
            return ResultBean.ofSuccess(null, "资料增补发起成功");
        }

        // 【提交】资料增补单
        else if (INFO_SUPPLEMENT.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {
            // 提交增补单
            endInfoSupplement(approval);
            return ResultBean.ofSuccess(null, "资料增补提交成功");
        }

        return ResultBean.ofSuccess(null, "action参数有误");
    }

    /**
     * 推送
     *
     * @param loanOrderDO
     * @param taskDefinitionKey
     * @param action
     * @param approval
     */
    private void push(LoanOrderDO loanOrderDO, String taskDefinitionKey, Byte action, ApprovalParam approval) {
        Long baseInfoId = loanOrderDO.getLoanBaseInfoId();
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseInfoId);
        if (loanBaseInfoDO != null) {
            StringBuffer cstr = new StringBuffer("客户:");
            StringBuffer bstr = new StringBuffer("您所提交的");
            StringBuffer msg = new StringBuffer("");
            LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), new Byte("0"));
            String customerName = null;
            if (loanCustomerDO != null) {
                customerName = loanCustomerDO.getName();
            }
            cstr.append("<" + customerName + ">").append(LoanProcessEnum.getNameByCode(taskDefinitionKey));
            bstr.append(LoanProcessEnum.getNameByCode(taskDefinitionKey));
            //0 未知 1 正常 2 提示  3 错误 4 警告
            //* 审核结果：0-REJECT / 1-PASS / 2-CANCEL / 3-资料增补
            Byte type = new Byte("0");
            //贷款信息不为空时候才会进行push 不然不知道推给谁
            if (action.intValue() == 0) {
                cstr.append("被打回");
                bstr.append("被打回");
                msg.append(approval.getInfo());
                type = new Byte("3");
            }

            if (action.intValue() == 1) {
                cstr.append("审核通过");
                bstr.append("审核通过");
                msg.append(approval.getInfo());
                type = new Byte("1");
            }

            if (action.intValue() == 2) {
                cstr.append("弃单");
                bstr.append("弃单");
                msg.append(approval.getInfo());
                type = new Byte("4");
            }

            if (action.intValue() == 3) {
                cstr.append("需要资料增补");
                bstr.append("需要资料增补");
                msg.append("增补说明" + approval.getSupplementInfo() + NEW_LINE + "内容:" + approval.getSupplementContent());
                type = new Byte("2");
            }

            jpushService.push(loanBaseInfoDO.getSalesmanId(), loanOrderDO.getId(), cstr.toString(), bstr.toString(), msg.toString(), taskDefinitionKey, type);
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
        // 电审任务
        if (TELEPHONE_VERIFY.getCode().equals(approval.getTaskDefinitionKey())) {
            // 执行电审任务
            execTelephoneVerifyTask(task, variables, approval, loanOrderDO.getId(), loanOrderDO.getLoanFinancialPlanId());
        } else {
            // 其他任务：直接提交
            completeTask(task, variables, approval);
        }

        // 征信申请记录拦截
        execCreditRecordFilterTask(task, loanOrderDO.getProcessInstId(), approval, variables);

        // 业务申请 & 上门调查 拦截
        execLoanApplyVisitVerifyFilterTask(task, loanOrderDO.getProcessInstId(), approval, variables);
    }

    /**
     * 完成任务
     *
     * @param task
     * @param variables
     * @param approval
     */
    private void completeTask(Task task, Map<String, Object> variables, ApprovalParam approval) {
        // 先获取提交之前的待执行任务列表
        List<Task> startTaskList = taskService.createTaskQuery()
                .processInstanceId(task.getProcessInstanceId())
                .list();

        // 执行任务
        taskService.complete(task.getId(), variables);

        // 起始任务ID列表
        List<String> startTaskIdList = startTaskList.parallelStream()
                .filter(Objects::nonNull)
                .map(e -> {
                    return e.getId();
                })
                .collect(Collectors.toList());

        // 更新状态
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(approval.getOrderId());

        // 如果弃单，则记录弃单节点
        if (ACTION_CANCEL.equals(approval.getAction())) {
            loanProcessDO.setOrderStatus(ORDER_STATUS_CANCEL);
            loanProcessDO.setCancelTaskDefKey(approval.getTaskDefinitionKey());
        }

        // 结单 ending  -暂无【结单节点】
//        if (XXX.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {
//            loanProcessDO.setOrderStatus(ORDER_STATUS_END);
//        }

        //【资料审核】打回到【业务申请】 标记
        if (MATERIAL_REVIEW.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_REJECT_MANUAL.equals(approval.getAction())) {
            loanProcessDO.setLoanApplyRejectOrginTask(MATERIAL_REVIEW.getCode());
        }

        // 更新当前执行的任务状态
        Byte taskProcessStatus = null;
        if (ACTION_REJECT_MANUAL.equals(approval.getAction()) || ACTION_REJECT_AUTO.equals(approval.getAction())) {
            taskProcessStatus = TASK_PROCESS_INIT;
        } else {
            taskProcessStatus = TASK_PROCESS_DONE;
        }
        updateCurrentTaskProcessStatus(loanProcessDO, approval.getTaskDefinitionKey(), taskProcessStatus);

        // 更新新产生的任务状态
        updateNextTaskProcessStatus(loanProcessDO, task.getProcessInstanceId(), startTaskIdList, approval.getAction(), approval.getTaskDefinitionKey());

        // 更新本地流程记录
        updateLoanProcess(loanProcessDO);
    }

    /**
     * 更新新产生的任务状态
     *
     * @param loanProcessDO
     * @param processInstanceId
     * @param startTaskIdList
     * @param action
     * @param taskDefinitionKey
     */
    private void updateNextTaskProcessStatus(LoanProcessDO loanProcessDO, String processInstanceId, List<String> startTaskIdList, Byte action, String taskDefinitionKey) {

        // 获取提交之后的待执行任务列表
        List<Task> endTaskList = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();

        if (CollectionUtils.isEmpty(endTaskList)) {
            return;
        }

        // 筛选出新产生和旧有的任务
        List<Task> newTaskList = Lists.newArrayList();
        List<Task> oldTaskList = Lists.newArrayList();
        endTaskList.parallelStream()
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

        if (ACTION_PASS.equals(action)) {
            // new  -> TO_DO   old -> 不变
            updateNextTaskProcessStatus(newTaskList, loanProcessDO, TASK_PROCESS_TODO);
        } else if (ACTION_REJECT_MANUAL.equals(action)) {
            // new  -> REJECT   old -> INIT
            updateNextTaskProcessStatus(newTaskList, loanProcessDO, TASK_PROCESS_REJECT);

            if (!MATERIAL_REVIEW.getCode().equals(taskDefinitionKey)) {
                updateNextTaskProcessStatus(oldTaskList, loanProcessDO, TASK_PROCESS_INIT);
            } else {
                // nothing
            }
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
     */
    private void updateNextTaskProcessStatus(List<Task> nextTaskList, LoanProcessDO loanProcessDO, Byte taskProcessStatus) {

        if (!CollectionUtils.isEmpty(nextTaskList)) {
            nextTaskList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(task -> {
                        // 未提交
                        updateCurrentTaskProcessStatus(loanProcessDO, task.getTaskDefinitionKey(), taskProcessStatus);
                    });
        }
    }

    /**
     * 更新本地已执行的任务状态
     *
     * @param loanProcessDO
     * @param taskDefinitionKey
     * @param taskProcessStatus
     */
    private void updateCurrentTaskProcessStatus(LoanProcessDO loanProcessDO, String taskDefinitionKey, Byte taskProcessStatus) {

        if (CREDIT_APPLY.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setCreditApply(taskProcessStatus);
        } else if (BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setBankCreditRecord(taskProcessStatus);
        } else if (SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setSocialCreditRecord(taskProcessStatus);
        } else if (LOAN_APPLY.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setLoanApply(taskProcessStatus);
        } else if (VISIT_VERIFY.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setVisitVerify(taskProcessStatus);
        } else if (TELEPHONE_VERIFY.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setTelephoneVerify(taskProcessStatus);
        } else if (BUSINESS_REVIEW.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setBusinessReview(taskProcessStatus);
        } else if (LOAN_REVIEW.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setLoanReview(taskProcessStatus);
        } else if (REMIT_REVIEW.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setRemitReview(taskProcessStatus);
        } else if (CAR_INSURANCE.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setCarInsurance(taskProcessStatus);
        } else if (APPLY_LICENSE_PLATE_DEPOSIT_INFO.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setApplyLicensePlateDepositInfo(taskProcessStatus);
        } else if (INSTALL_GPS.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setInstallGps(taskProcessStatus);
        } else if (COMMIT_KEY.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setCommitKey(taskProcessStatus);
        } else if (VEHICLE_INFORMATION.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setVehicleInformation(taskProcessStatus);
        } else if (BUSINESS_REVIEW.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setBusinessReview(taskProcessStatus);
        } else if (LOAN_REVIEW.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setLoanReview(taskProcessStatus);
        } else if (REMIT_REVIEW.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setRemitReview(taskProcessStatus);
        } else if (MATERIAL_REVIEW.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setMaterialReview(taskProcessStatus);
        } else if (MATERIAL_PRINT_REVIEW.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setMaterialPrintReview(taskProcessStatus);
        } else if (BANK_CARD_RECORD.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setBankCardRecord(taskProcessStatus);
        } else if (FINANCIAL_SCHEME.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setFinancialScheme(taskProcessStatus);
        } else if (BANK_LEND_RECORD.getCode().equals(taskDefinitionKey)) {
            loanProcessDO.setBankLendRecord(taskProcessStatus);
        }
    }


    /**
     * 创建增补单
     *
     * @param approval
     */
    private void startInfoSupplement(ApprovalParam approval) {
        Preconditions.checkNotNull(approval.getSupplementType(), "资料增补类型不能为空");
        Preconditions.checkNotNull(approval.getSupplementContent(), "资料增补内容不能为空");

        LoanInfoSupplementDO loanInfoSupplementDO = new LoanInfoSupplementDO();
        loanInfoSupplementDO.setOrderId(approval.getOrderId());

        // 发起人信息
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        loanInfoSupplementDO.setInitiatorId(loginUser.getId());
        loanInfoSupplementDO.setInitiatorName(loginUser.getName());
        loanInfoSupplementDO.setStartTime(new Date());

        // 增补信息
        loanInfoSupplementDO.setType(approval.getSupplementType());
        loanInfoSupplementDO.setContent(approval.getSupplementContent());
        loanInfoSupplementDO.setInfo(approval.getSupplementInfo());

        // 增补源头任务节点
        loanInfoSupplementDO.setOriginTask(approval.getTaskDefinitionKey());

        // 未处理状态
        loanInfoSupplementDO.setStatus(TASK_PROCESS_TODO);

        int count = loanInfoSupplementDOMapper.insertSelective(loanInfoSupplementDO);
        Preconditions.checkArgument(count > 0, "资料增补发起失败");
    }

    /**
     * 提交增补单
     *
     * @param approval
     */
    private void endInfoSupplement(ApprovalParam approval) {
        Preconditions.checkNotNull(approval.getSupplementOrderId(), "增补单ID不能为空");

        LoanInfoSupplementDO loanInfoSupplementDO = new LoanInfoSupplementDO();

        // 增补单ID
        loanInfoSupplementDO.setId(approval.getSupplementOrderId());

        // 增补人信息
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        loanInfoSupplementDO.setSupplementerId(loginUser.getId());
        loanInfoSupplementDO.setSupplementerName(loginUser.getName());
        loanInfoSupplementDO.setEndTime(new Date());

        // 审核备注
        loanInfoSupplementDO.setRemark(approval.getInfo());

        // 已处理状态
        loanInfoSupplementDO.setStatus(TASK_PROCESS_DONE);

        int count = loanInfoSupplementDOMapper.updateByPrimaryKeySelective(loanInfoSupplementDO);
        Preconditions.checkArgument(count > 0, "资料增补提交失败");
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
        Set<String> userGroupNameSet = getUserGroupNameSet();
        // 最大电审角色等级
        Byte maxRoleLevel = getTelephoneVerifyMaxRole(userGroupNameSet);
        // 电审专员及以上有权电审
        Preconditions.checkArgument(null != maxRoleLevel && maxRoleLevel >= LEVEL_TELEPHONE_VERIFY_COMMISSIONER, "您无电审权限");

        String taskId = task.getId();

        // 如果是审核通过
        if (ACTION_PASS.equals(approval.getAction())) {

            // 获取贷款额度
            LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanFinancialPlanId);
            Preconditions.checkArgument(null != loanFinancialPlanDO && null != loanFinancialPlanDO.getLoanAmount(), "贷款额不能为空");
            double loanAmount = loanFinancialPlanDO.getLoanAmount().doubleValue();

            // 直接通过
            if (loanAmount >= 0 && loanAmount <= 100000) {
                // 完成任务：全部角色直接过单
                passTelephoneVerifyTask(task, variables, approval);
            } else if (loanAmount > 100000 && loanAmount <= 300000) {
                // 电审主管以上可过单
                if (maxRoleLevel < LEVEL_TELEPHONE_VERIFY_LEADER) {
                    // 记录
                    updateTelephoneVerify(orderId, maxRoleLevel, taskId, variables);
                } else {
                    // 完成任务
                    passTelephoneVerifyTask(task, variables, approval);
                }
            } else if (loanAmount > 300000 && loanAmount <= 500000) {
                // 电审经理以上可过单
                if (maxRoleLevel < LEVEL_TELEPHONE_VERIFY_MANAGER) {
                    // 记录
                    updateTelephoneVerify(orderId, maxRoleLevel, taskId, variables);
                } else {
                    // 完成任务
                    passTelephoneVerifyTask(task, variables, approval);
                }
            } else if (loanAmount > 500000) {
                // 总监以上可过单
                if (maxRoleLevel < LEVEL_DIRECTOR) {
                    // 记录
                    updateTelephoneVerify(orderId, maxRoleLevel, taskId, variables);
                } else {
                    // 完成任务
                    passTelephoneVerifyTask(task, variables, approval);
                }
            }
        } else if (ACTION_CANCEL.equals(approval.getAction())) {
            // 弃单，直接提交
            completeTask(task, variables, approval);
        } else if (ACTION_REJECT_MANUAL.equals(approval.getAction())) {
            // 手动打回
            variables.put(PROCESS_VARIABLE_TARGET, LOAN_APPLY.getCode());
            completeTask(task, variables, approval);
        } else if (ACTION_REJECT_AUTO.equals(approval.getAction())) {
            // 自动打回
            variables.put(PROCESS_VARIABLE_TARGET, CREDIT_APPLY.getCode());
            completeTask(task, variables, approval);
        }
    }

    /**
     * 【电审】过单
     *
     * @param task
     * @param variables
     * @param approval
     */
    private void passTelephoneVerifyTask(Task task, Map<String, Object> variables, ApprovalParam approval) {
        // 完成任务：全部角色直接过单
        completeTask(task, variables, approval);
        // 自动执行【金融方案】任务
        completeFinancialSchemeTask(task.getProcessInstanceId(), approval.getOrderId());
    }

    /**
     * 自动执行【金融方案】任务
     *
     * @param processInstanceId
     * @param orderId
     */
    private void completeFinancialSchemeTask(String processInstanceId, Long orderId) {

        Map<String, Object> variables = Maps.newHashMap();
        variables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(FINANCIAL_SCHEME.getCode())
                .singleResult();

        Preconditions.checkNotNull(task, "[金融方案]任务不存在");

        ApprovalParam approval = new ApprovalParam();
        approval.setOrderId(orderId);
        approval.setAction(ACTION_PASS);
        approval.setTaskDefinitionKey(FINANCIAL_SCHEME.getCode());

        completeTask(task, variables, approval);
    }

    /**
     * 更新电审流程
     *
     * @param orderId
     * @param telephoneVerifyProcess
     * @param taskId
     * @param variables
     */
    private void updateTelephoneVerify(Long orderId, Byte telephoneVerifyProcess, String taskId, Map<String, Object> variables) {
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(orderId);
        loanProcessDO.setTelephoneVerify(telephoneVerifyProcess);
        // 本地表更新
        updateLoanProcess(loanProcessDO);
    }

    /**
     * 获取改账号在【电审】中最大角色level
     *
     * @param userGroupNameSet
     * @return
     */
    private Byte getTelephoneVerifyMaxRole(Set<String> userGroupNameSet) {
        if (CollectionUtils.isEmpty(userGroupNameSet)) {
            return null;
        }

        final Byte[] maxLevel = {0};

        userGroupNameSet.stream()
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
    public Set<String> getUserGroupNameSet() {
        // getUser
        EmployeeDO loginUser = SessionUtils.getLoginUser();

        // 员工-直接关联的用户组
        List<String> userGroupNameList = userGroupDOMapper.listUserGroupNameByEmployeeId(loginUser.getId());

        // 员工-所属部门 -间接关联的用户组
        List<String> userGroupNameList_ = userGroupDOMapper.listUserGroupNameByEmployeeIdRelaDepartment(loginUser.getId());

        Set<String> allUserGroupNameList = Sets.newHashSet();
        allUserGroupNameList.addAll(userGroupNameList);
        allUserGroupNameList.addAll(userGroupNameList_);

        return allUserGroupNameList;
    }

    /**
     * 业务申请 & 上门调查 拦截
     *
     * @param currentTask
     * @param processInstId
     * @param approval
     * @param variables
     */
    private void execLoanApplyVisitVerifyFilterTask(Task currentTask, String processInstId, ApprovalParam approval, Map<String, Object> variables) {

        // 执行拦截任务
        if (isLoanApplyVisitVerifyFilterTask(approval.getTaskDefinitionKey(), variables)) {
            // 获取所有正在执行的并行任务
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstId)
                    .list();

            // 上门调查：只有【提交】;  业务申请：只有【提交】&【弃单】;      -均无[打回]
            // PASS
            if (ACTION_PASS.equals(approval.getAction())) {
                dealLoanApplyVisitVerifyFilterPassTask(currentTask, tasks, approval);
            }
            // CANCEL
            else if (ACTION_CANCEL.equals(approval.getAction())) {
                dealCancelTask(processInstId);
            }
            // AUTO_REJECT
            else if (ACTION_REJECT_AUTO.equals(approval.getAction())) {
                dealLoanApplyVisitVerifyAutoRejectTask(currentTask, tasks, approval);
            }
        }
    }

    /**
     * 【业务申请】&【上门调查】自动打回任务
     *
     * @param currentTask
     * @param tasks
     * @param approvalParam
     */
    private void dealLoanApplyVisitVerifyAutoRejectTask(Task currentTask, List<Task> tasks, ApprovalParam approvalParam) {
        // 打回
        dealLoanApplyVisitVerifyAutoRejectTask(currentTask, tasks);

        // update process
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(approvalParam.getOrderId());
        loanProcessDO.setLoanApply(TASK_PROCESS_INIT);
        loanProcessDO.setVisitVerify(TASK_PROCESS_INIT);
        updateLoanProcess(loanProcessDO);

        // 自动提交打回的【征信申请】
        approvalParam.setTaskDefinitionKey(CREDIT_APPLY.getCode());
        approvalParam.setAction(ACTION_PASS);
        approvalParam.setNeedLog(false);
        approval(approvalParam);
    }

    /**
     * 并行任务：-通过
     *
     * @param currentTask
     * @param tasks
     * @param approval
     */
    private void dealLoanApplyVisitVerifyFilterPassTask(Task currentTask, List<Task> tasks, ApprovalParam approval) {

        // 是否都通过了      -> 既非LOAN_APPLY，也非VISIT_VERIFY
        if (!CollectionUtils.isEmpty(tasks)) {
            long count = tasks.parallelStream()
                    .filter(Objects::nonNull)
                    .filter(e -> LOAN_APPLY.getCode().equals(e.getTaskDefinitionKey()) || VISIT_VERIFY.getCode().equals(e.getTaskDefinitionKey()))
                    .count();

            // 是 -> 放行
            if (count == 0) {

                // 当前已经不会有子任务为：LOAN_APPLY或VISIT_VERIFY     只需从所有filter任务中找到-主任务 -> 通过！  然后剩余"filter子任务"全部弃单即可！
                Map<String, Object> passVariables = Maps.newHashMap();
                Map<String, Object> cancelVariables = Maps.newHashMap();
                passVariables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);
                cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);

                tasks.stream()
                        .filter(Objects::nonNull)
                        .forEach(task -> {

                            // 拿到当前"主任务"
                            if (currentTask.getExecutionId().equals(task.getExecutionId())) {
                                // "主任务"  ->  通过
                                completeTask(task, passVariables, approval);
                            } else if (LOAN_APPLY_VISIT_VERIFY_FILTER.getCode().equals(task.getTaskDefinitionKey())
                                    && !currentTask.getExecutionId().equals(task.getExecutionId())) {
                                // 其他filter"子任务"全部弃掉
                                taskService.complete(task.getId(), cancelVariables);
                            }
                        });
            }

            // 否 -> 等待  不做处理
        }
    }

    /**
     * 征信申请记录拦截
     *
     * @param currentTask
     * @param processInstId
     * @param approval
     * @param variables
     */
    private void execCreditRecordFilterTask(Task currentTask, String processInstId, ApprovalParam approval, Map<String, Object> variables) {

        // 执行拦截任务
        if (isBankAndSocialCreditRecordTask(variables, approval.getTaskDefinitionKey())) {

            // 获取所有正在执行的并行任务
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstId)
                    .list();

            // PASS
            if (ACTION_PASS.equals(approval.getAction())) {
                dealPassTask(currentTask, tasks, approval);
            }
            // REJECT
            else if (ACTION_REJECT_MANUAL.equals(approval.getAction())) {
                dealRejectTask(currentTask, tasks);
            }
            // CANCEL
            else if (ACTION_CANCEL.equals(approval.getAction())) {
                dealCancelTask(currentTask.getProcessInstanceId());
            }
            // AUTO_REJECT
            else if (ACTION_REJECT_AUTO.equals(approval.getAction())) {
                dealCreditRecordAutoRejectTask(currentTask, tasks, approval);
            }
        }

        // 小于13W:  单银行征信录入 & 自动打回操作时
        else if (isOnlyOneBankCreditRecordTask(variables, approval.getTaskDefinitionKey())) {
            // AUTO_REJECT
            if (ACTION_REJECT_AUTO.equals(approval.getAction())) {

                // 获取所有正在执行的并行任务
                List<Task> tasks = taskService.createTaskQuery()
                        .processInstanceId(processInstId)
                        .list();

                dealCreditRecordAutoRejectTask(currentTask, tasks, approval);
            }
        }
    }

    /**
     * 【银行征信】&【社会征信】自动打回任务
     *
     * @param currentTask
     * @param tasks
     * @param approvalParam
     */

    private void dealCreditRecordAutoRejectTask(Task currentTask, List<Task> tasks, ApprovalParam approvalParam) {
        // 打回
        dealRejectTask(currentTask, tasks);

        // update process
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(approvalParam.getOrderId());
        loanProcessDO.setBankCreditRecord(TASK_PROCESS_INIT);
        loanProcessDO.setSocialCreditRecord(TASK_PROCESS_INIT);
        updateLoanProcess(loanProcessDO);

        // 自动提交打回的【征信申请】
        approvalParam.setTaskDefinitionKey(CREDIT_APPLY.getCode());
        approvalParam.setAction(ACTION_PASS);
        approvalParam.setNeedLog(false);
        approval(approvalParam);
    }

    @Override
    public ResultBean<List<TaskStateVO>> currentTask(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        List<Task> runTaskList = taskService.createTaskQuery()
                .processInstanceId(loanOrderDO.getProcessInstId())
                .list();

        List<TaskStateVO> taskStateVOS = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(runTaskList)) {
            taskStateVOS = runTaskList.parallelStream()
                    .filter(Objects::nonNull)
                    .filter(task -> !BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode().equals(task.getTaskDefinitionKey())
                            && !LOAN_APPLY_VISIT_VERIFY_FILTER.getCode().equals(task.getTaskDefinitionKey()))
                    .map(task -> {

                        TaskStateVO taskStateVO = new TaskStateVO();
                        taskStateVO.setTaskDefinitionKey(task.getTaskDefinitionKey());
                        taskStateVO.setTaskName(task.getName());
                        taskStateVO.setTaskStatus(TASK_PROCESS_TODO);
                        taskStateVO.setTaskStatusText(getTaskStatusText(TASK_PROCESS_TODO));

                        return taskStateVO;
                    })
                    .collect(Collectors.toList());
        }

        // TODO 还要加上资料增补任务 ？？？

        return ResultBean.ofSuccess(taskStateVOS, "查询当前流程任务节点信息成功");
    }

    @Override
    public ResultBean<TaskStateVO> taskStatus(Long orderId, String taskDefinitionKey) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(taskDefinitionKey), "任务ID不能为空");

        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

        TaskStateVO taskStateVO = new TaskStateVO();
        taskStateVO.setTaskDefinitionKey(taskDefinitionKey);
        taskStateVO.setTaskName(LoanProcessEnum.getNameByCode(taskDefinitionKey));

        Byte taskStatus = null;

        // 非进行中
        if (!ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus())) {

            if (ORDER_STATUS_END.equals(loanProcessDO.getOrderStatus())) {
                taskStatus = 11;
                taskStateVO.setTaskStatus(taskStatus);
                taskStateVO.setTaskStatusText("已结单");
            }

            if (ORDER_STATUS_CANCEL.equals(loanProcessDO.getOrderStatus())) {
                taskStatus = 12;
                taskStateVO.setTaskStatus(taskStatus);
                taskStateVO.setTaskStatusText("已弃单");
            }

        } else {
            // 进行中
            if (CREDIT_APPLY.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getCreditApply();
            } else if (BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getBankCreditRecord();
            } else if (SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getSocialCreditRecord();
            } else if (LOAN_APPLY.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getLoanApply();
            } else if (VISIT_VERIFY.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getVisitVerify();
            } else if (TELEPHONE_VERIFY.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getTelephoneVerify();
            } else if (BUSINESS_REVIEW.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getBusinessReview();
            } else if (LOAN_REVIEW.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getLoanReview();
            } else if (REMIT_REVIEW.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getRemitReview();
            } else if (CAR_INSURANCE.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getCarInsurance();
            } else if (APPLY_LICENSE_PLATE_DEPOSIT_INFO.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getApplyLicensePlateDepositInfo();
            } else if (INSTALL_GPS.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getInstallGps();
            } else if (COMMIT_KEY.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getCommitKey();
            } else if (VEHICLE_INFORMATION.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getVehicleInformation();
            } else if (BUSINESS_REVIEW.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getBusinessReview();
            } else if (LOAN_REVIEW.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getLoanReview();
            } else if (REMIT_REVIEW.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getRemitReview();
            } else if (MATERIAL_REVIEW.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getMaterialReview();
            } else if (MATERIAL_PRINT_REVIEW.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getMaterialPrintReview();
            } else if (BANK_LEND_RECORD.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getBankLendRecord();
            } else if (BANK_CARD_RECORD.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getBankCardRecord();
            } else if (FINANCIAL_SCHEME.getCode().equals(taskDefinitionKey)) {
                taskStatus = loanProcessDO.getFinancialScheme();
            }

            taskStateVO.setTaskStatus(taskStatus);
            taskStateVO.setTaskStatusText(getTaskStatusText(taskStatus));
        }

        return ResultBean.ofSuccess(taskStateVO, "查询当前流程任务节点状态成功");
    }

    /**
     * 任务状态文本值
     *
     * @param taskStatus
     * @return
     */
    private String getTaskStatusText(Byte taskStatus) {
        String taskStatusText = null;
        switch (taskStatus) {
            case 0:
                taskStatusText = "未执行到此";
                break;
            case 1:
                taskStatusText = "已提交";
                break;
            case 2:
                taskStatusText = "未提交";
                break;
            case 3:
                taskStatusText = "已打回";
                break;
            // null
            default:
                taskStatusText = "-";
                break;
        }
        return taskStatusText;
    }

    @Override
    public ResultBean<List<String>> orderHistory(Long orderId, Integer limit) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        List<LoanProcessLogDO> loanProcessLogDOList = loanProcessLogDOMapper.listByOrderId(orderId, limit);

        List<String> historyList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(loanProcessLogDOList)) {

            loanProcessLogDOList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        // 王希  于 2017-12-29 13:07:00  创建 xx业务信息
                        String history = e.getUserName()
                                + " 于 "
                                + convertApprovalDate(e.getCreateTime())
                                + " "
                                + convertActionText(e.getAction())
                                + " "
                                + convertTaskDefKeyText(e.getTaskDefinitionKey());

                        historyList.add(history);
                    });
        }

        return ResultBean.ofSuccess(historyList, "订单日志查询成功");
    }

    @Override
    public ResultBean<LoanProcessLogVO> log(Long orderId, String taskDefinitionKey) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(taskDefinitionKey), "任务节点不能为空");

        LoanProcessLogDO loanProcessLogDO = loanProcessLogDOMapper.lastLogByOrderIdAndTaskDefinitionKey(orderId, taskDefinitionKey);

        LoanProcessLogVO loanProcessLogVO = new LoanProcessLogVO();
        if (null != loanProcessLogDO) {
            BeanUtils.copyProperties(loanProcessLogDO, loanProcessLogVO);
        }

        return ResultBean.ofSuccess(loanProcessLogVO);
    }

    /**
     * action字面意义转换
     *
     * @param action 0-打回; 1-通过(提交); 2-弃单; 3-资料增补;
     * @return
     */
    private String convertActionText(Byte action) {
        String actionText = null;

        switch (action) {
            case 0:
                actionText = "打回";
                break;
            case 1:
                actionText = "提交";
                break;
            case 2:
                actionText = "弃单";
                break;
            case 3:
                actionText = "资料增补";
                break;
            default:
                actionText = "-";
        }
        return actionText;
    }

    /**
     * 审核时间转换
     *
     * @param approvalDate
     * @return
     */
    private String convertApprovalDate(Date approvalDate) {
        String approvalDateStr = null;
        if (null != approvalDate) {
            approvalDateStr = DateFormatUtils.format(approvalDate, "yyyy-MM-dd HH:mm:ss");
        }
        return approvalDateStr;
    }

    /**
     * 任务名称转换
     *
     * @param taskDefinitionKey
     * @return
     */
    private String convertTaskDefKeyText(String taskDefinitionKey) {
        String name = LoanProcessEnum.getNameByCode(taskDefinitionKey);
        return name;
    }

    /**
     * 执行 征信申请审核 或 银行&社会征信录入 任务时：填充流程变量-贷款金额
     *
     * @param variables
     * @param action
     * @param taskDefinitionKey
     * @param loanBaseInfoId
     */
    private void fillLoanAmount(Map<String, Object> variables, Byte action, String taskDefinitionKey, Long loanBaseInfoId) {
        // 征信申请且审核通过时
        boolean isApplyVerifyTaskAndActionIsPass = CREDIT_APPLY.getCode().equals(taskDefinitionKey) && ACTION_PASS.equals(action);
        // 银行&社会征信录入
        boolean isBankAndSocialCreditRecordTask = BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey) || SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey);
        if (isApplyVerifyTaskAndActionIsPass || isBankAndSocialCreditRecordTask) {
            // 贷款金额
            LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);
            Preconditions.checkNotNull(loanBaseInfoDO, "数据异常，贷款基本信息为空");
            Preconditions.checkNotNull(loanBaseInfoDO.getLoanAmount(), "数据异常，贷款金额为空");
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

        if (INFO_SUPPLEMENT.getCode().equals(taskDefinitionKey)) {
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstId)
                    .listPage(0, 1);
            Preconditions.checkArgument(!CollectionUtils.isEmpty(tasks), "订单已结清或已弃单");
            return tasks.get(0);
        }

        // 获取当前流程taskId
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstId)
                .taskDefinitionKey(taskDefinitionKey)
                .singleResult();

        Preconditions.checkNotNull(task, "当前任务不存在");
        return task;
    }

    /**
     * 设置并返回流程变量
     *
     * @param currentExecTask
     * @param approval
     * @param loanBaseInfoId
     */
    private Map<String, Object> setAndGetVariables(Task currentExecTask, ApprovalParam approval, Long loanBaseInfoId) {
        Map<String, Object> variables = Maps.newHashMap();

        // 流程变量：action
        variables.put(PROCESS_VARIABLE_ACTION, approval.getAction());

        // 添加流程变量-贷款金额
        fillLoanAmount(variables, approval.getAction(), currentExecTask.getTaskDefinitionKey(), loanBaseInfoId);

        // 填充其他的流程变量
        fillOtherVariables(variables, approval);

        return variables;
    }

    /**
     * 填充其他的流程变量
     *
     * @param variables
     * @param approval
     */
    private void fillOtherVariables(Map<String, Object> variables, ApprovalParam approval) {
        // 业务申请
        if (LOAN_APPLY.getCode().equals(approval.getTaskDefinitionKey())) {

            // 是否 [打回] - 自于【资料审核】

            // 1.是否为打回
            LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(approval.getOrderId());
            Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

            Byte loanApply = loanProcessDO.getLoanApply();
            if (TASK_PROCESS_REJECT.equals(loanApply)) {

                // 2.是否打回自【资料审核】
                String loanApplyRejectOrginTask = loanProcessDO.getLoanApplyRejectOrginTask();
                // 是
                if (MATERIAL_REVIEW.getCode().equals(loanApplyRejectOrginTask)) {
                    // 添加流程变量 -打回来源 -> reject_origin_task
                    variables.put(PROCESS_VARIABLE_REJECT_ORIGIN_TASK, MATERIAL_REVIEW.getCode());

                    // 将reject_origin_task置空
                    updateLoanApplyRejectOrginTaskIsNull(loanProcessDO);
                } else {
                    // 否
                    variables.put(PROCESS_VARIABLE_REJECT_ORIGIN_TASK, null);
                }
            } else {
                // 否
                variables.put(PROCESS_VARIABLE_REJECT_ORIGIN_TASK, null);
            }
        }
    }

    /**
     * 将reject_origin_task置空
     *
     * @param loanProcessDO
     */
    private void updateLoanApplyRejectOrginTaskIsNull(LoanProcessDO loanProcessDO) {
        int count = loanProcessDOMapper.updateLoanApplyRejectOrginTaskIsNull(loanProcessDO.getOrderId());
        Preconditions.checkArgument(count > 0, "更新失败");
    }

    /**
     * 更新本地流程记录
     *
     * @param loanProcessDO
     */
    private void updateLoanProcess(LoanProcessDO loanProcessDO) {
        loanProcessDO.setGmtModify(new Date());
        int count = loanProcessDOMapper.updateByPrimaryKeySelective(loanProcessDO);
        Preconditions.checkArgument(count > 0, "更新本地流程记录失败");
    }

    /**
     * 是否为：银行&社会征信并行任务   大于13W
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
     * 小于13W
     *
     * @param variables
     * @param taskDefinitionKey
     * @return
     */
    public boolean isOnlyOneBankCreditRecordTask(Map<String, Object> variables, String taskDefinitionKey) {
        Byte loanAmount = (Byte) variables.get("loanAmount");
        boolean isOnlyOneBankCreditRecordTask = (BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey) || SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey))
                && null != loanAmount && loanAmount == 1;
        return isOnlyOneBankCreditRecordTask;
    }


    /**
     * 是否为：业务审核&资料审核 并行任务
     *
     * @param taskDefinitionKey
     * @return
     */
    private boolean isBusinessMaterialReviewFilterTask(String taskDefinitionKey) {
        boolean isBusinessMaterialReviewFilterTask = (BUSINESS_REVIEW.getCode().equals(taskDefinitionKey) || MATERIAL_REVIEW.getCode().equals(taskDefinitionKey));
        return isBusinessMaterialReviewFilterTask;
    }

    /**
     * 是否为：业务申请&上门调查 并行任务   -业务申请 或 上门调查  但是非【资料审核】打回
     *
     * @param taskDefinitionKey
     * @param variables
     * @return
     */
    private boolean isLoanApplyVisitVerifyFilterTask(String taskDefinitionKey, Map<String, Object> variables) {
        // 业务申请 或 上门调查    但是非【资料审核】打回
        boolean isLoanApplyVisitVerifyFilterTask = (LOAN_APPLY.getCode().equals(taskDefinitionKey) || VISIT_VERIFY.getCode().equals(taskDefinitionKey))
                && !MATERIAL_REVIEW.getCode().equals(variables.get(PROCESS_VARIABLE_REJECT_ORIGIN_TASK));
        return isLoanApplyVisitVerifyFilterTask;
    }

    /**
     * 并行任务 -通过
     *
     * @param currentTask
     * @param tasks
     * @param approval
     */

    private void dealPassTask(Task currentTask, List<Task> tasks, ApprovalParam approval) {

        // 是否都通过了    -> 既非BANK，也非SOCIAL
        if (!CollectionUtils.isEmpty(tasks)) {

            long count = tasks.parallelStream()
                    .filter(Objects::nonNull)
                    .filter(e -> BANK_CREDIT_RECORD.getCode().equals(e.getTaskDefinitionKey()) || SOCIAL_CREDIT_RECORD.getCode().equals(e.getTaskDefinitionKey()))
                    .count();

            // 是 -> 放行
            if (count == 0) {

                // 当前已经不会有子任务为：BANK或SOCIAL    只需从所有filter任务中找到-主任务 -> 通过即可！  然后剩余"filter子任务"全部弃单即可！
                Map<String, Object> passVariables = Maps.newHashMap();
                Map<String, Object> cancelVariables = Maps.newHashMap();
                passVariables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);
                cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);

                tasks.stream()
                        .filter(Objects::nonNull)
                        .forEach(task -> {

                            // 拿到当前"主任务"
                            if (currentTask.getExecutionId().equals(task.getExecutionId())) {
                                // "主任务"  ->  通过
                                completeTask(task, passVariables, approval);
                            } else if (BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode().equals(task.getTaskDefinitionKey())
                                    && !currentTask.getExecutionId().equals(task.getExecutionId())) {
                                // 其他filter"子任务"全部弃掉
                                taskService.complete(task.getId(), cancelVariables);
                            }
                        });
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

                Map<String, Object> cancelVariables = Maps.newHashMap();

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
            Map<String, Object> rejectVariables = Maps.newHashMap();
            rejectVariables.put(PROCESS_VARIABLE_ACTION, ACTION_REJECT_MANUAL);
            taskService.complete(currentFilterTask.getId(), rejectVariables);
        }
    }

    /**
     * @param currentTask
     * @param tasks
     */
    private void dealLoanApplyVisitVerifyAutoRejectTask(Task currentTask, List<Task> tasks) {

        // 打回 -> 先提交-再弃单掉其他子任务， 然后打回
        if (!CollectionUtils.isEmpty(tasks)) {

            // 仅保留一个子任务  当做 -> 主任务
            final Task[] onlyAsMainTask = {null};

            // 其他子任务全部弃掉
            if (!CollectionUtils.isEmpty(tasks)) {

                Map<String, Object> passVariables = Maps.newHashMap();
                Map<String, Object> cancelVariables = Maps.newHashMap();
                passVariables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);
                cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);

                tasks.stream()
                        .filter(Objects::nonNull)
                        .forEach(task -> {

                            if (currentTask.getExecutionId().equals(task.getExecutionId())) {
                                // 拿到当前子任务
                                onlyAsMainTask[0] = task;
                            } else if (LOAN_APPLY_VISIT_VERIFY_FILTER.getCode().equals(task.getTaskDefinitionKey())) {
                                // 其他filter任务 -> 弃单
                                taskService.complete(task.getId(), cancelVariables);
                            } else {
                                // 子任务 -> 全部提交
                                taskService.complete(task.getId(), passVariables);
                            }

                        });
            }

            List<Task> taskList = taskService.createTaskQuery()
                    .processInstanceId(currentTask.getProcessInstanceId())
                    .list();

            if (!CollectionUtils.isEmpty(taskList)) {

                Map<String, Object> cancelVariables = Maps.newHashMap();
                cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);

                // filter任务
                taskList.stream()
                        .filter(Objects::nonNull)
                        .filter(task -> LOAN_APPLY_VISIT_VERIFY_FILTER.getCode().equals(task.getTaskDefinitionKey())
                                && !currentTask.getExecutionId().equals(task.getExecutionId()))
                        .forEach(task -> {
                            // 其他filter任务 -> 弃单
                            taskService.complete(task.getId(), cancelVariables);
                        });
            }

            // "主任务"  ->  自动打回
            Task currentFilterTask = onlyAsMainTask[0];
            Map<String, Object> autuRejectVariables = Maps.newHashMap();
            autuRejectVariables.put(PROCESS_VARIABLE_ACTION, ACTION_REJECT_AUTO);
            taskService.complete(currentFilterTask.getId(), autuRejectVariables);
        }
    }

    /**
     * 并行任务 -弃单
     *
     * @param processInstanceId
     */
    private void dealCancelTask(String processInstanceId) {
        // 弃单 -> 直接终止流程
        runtimeService.deleteProcessInstance(processInstanceId, "弃单");
    }
}
