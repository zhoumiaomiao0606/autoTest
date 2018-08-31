package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.StringUtil;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.entity.TaskDistributionDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.mapper.TaskDistributionDOMapper;
import com.yunche.loan.service.LoanProcessApprovalCommonService;
import com.yunche.loan.service.LoanProcessApprovalRollBackService;
import com.yunche.loan.service.LoanProcessService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_INIT;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_TODO;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.APPLY_INSTALMENT;
import static com.yunche.loan.config.constant.LoanProcessEnum.DATA_FLOW_CONTRACT_C2B;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_ACTION;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_TARGET;
import static com.yunche.loan.config.constant.ProcessApprovalConst.*;
import static com.yunche.loan.config.constant.TaskDistributionConst.TASK_STATUS_DOING;
import static com.yunche.loan.config.constant.TaskDistributionConst.TASK_STATUS_DONE;

/**
 * @author liuzhe
 * @date 2018/8/30
 */
@Service
public class LoanProcessApprovalRollBackServiceImpl implements LoanProcessApprovalRollBackService {


    @Autowired
    private TaskDistributionDOMapper taskDistributionDOMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private LoanProcessService loanProcessService;

    @Autowired
    private LoanProcessApprovalCommonService loanProcessApprovalCommonService;


    /**
     * 执行[反审]
     *
     * @param approval
     * @param loanOrderDO
     * @param loanProcessDO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBean<Void> execRollBackTask(ApprovalParam approval, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        // 先获取提交之前的待执行任务列表
        List<String> currentTaskIdList = loanProcessApprovalCommonService.getCurrentTaskIdList(loanOrderDO.getProcessInstId());

        String taskDefinitionKey = approval.getTaskDefinitionKey();

        // [业务申请]
        if (LOAN_APPLY.getCode().equals(taskDefinitionKey)) {

            // [业务申请] 必须已提交
            Byte loanApplyStatus = loanProcessDO.getLoanApply();
            Preconditions.checkArgument(TASK_PROCESS_DONE.equals(loanApplyStatus), "操作错误：[业务申请]还未提交,不能发起[反审]");

            // [电审]任务状态
            Byte telephoneVerifyStatus = loanProcessDO.getTelephoneVerify();

            // 1
            if (TASK_PROCESS_DONE.equals(telephoneVerifyStatus)) {
                throw new BizException("[电审]已完成，无法发起[反审]");
            }

            // 3   -不存在该状态

            // 4、5、6、7
            else if (TASK_PROCESS_TELEPHONE_VERIFY_COMMISSIONER.equals(telephoneVerifyStatus)
                    || TASK_PROCESS_TELEPHONE_VERIFY_LEADER.equals(telephoneVerifyStatus)
                    || TASK_PROCESS_TELEPHONE_VERIFY_MANAGER.equals(telephoneVerifyStatus)
                    || TASK_PROCESS_TELEPHONE_VERIFY_DIRECTOR.equals(telephoneVerifyStatus)) {

                throw new BizException("[电审]任务已审核，无法发起[反审]");
            }

            // 2     -[电审]中
            // 已流转到[电审]，但未被领取的时候，可进行-[反审]
            else if (TASK_PROCESS_TODO.equals(telephoneVerifyStatus)) {

                // [电审]领取
                TaskDistributionDO taskDistributionDO = taskDistributionDOMapper.selectByPrimaryKey(approval.getOrderId(), TELEPHONE_VERIFY.getCode());

                if (null != taskDistributionDO) {
                    Preconditions.checkArgument(TASK_STATUS_DOING.equals(taskDistributionDO.getStatus()), "[电审]任务已被领取，无法发起[反审]");
                    Preconditions.checkArgument(TASK_STATUS_DONE.equals(taskDistributionDO.getStatus()), "[电审]任务已完成，无法发起[反审]");
                }

                // [电审]-反审 自动打回至[贷款申请]和[上门调查]
                autoPassTask(approval.getOrderId(), TELEPHONE_VERIFY.getCode(), ACTION_REJECT_AUTO);

                // update process
                LoanProcessDO loanProcessDO_ = new LoanProcessDO();
                loanProcessDO_.setOrderId(approval.getOrderId());
                loanProcessDO_.setTelephoneVerify(TASK_PROCESS_INIT);
                loanProcessDO_.setLoanApply(TASK_PROCESS_TODO);
                loanProcessDO_.setVisitVerify(TASK_PROCESS_TODO);
                loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO_);
            }

            // 0     -[电审]前   ==> 即：[上门调查]未提交
            else if (TASK_PROCESS_INIT.equals(telephoneVerifyStatus)) {

                // [业务申请] -> 执行[反审]
                doLoanApplyVisitVerifyFilterTask_RollBack(loanOrderDO);

                // update process
                LoanProcessDO loanProcess = new LoanProcessDO();
                loanProcess.setOrderId(approval.getOrderId());
                loanProcess.setLoanApply(TASK_PROCESS_TODO);
                loanProcess.setVisitVerify(TASK_PROCESS_TODO);
                loanProcessApprovalCommonService.updateLoanProcess(loanProcess);

            } else {

                throw new BizException("发起[业务申请-反审]异常");
            }

        }

        // [银行征信录入] || [社会征信录入]
        else if (BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey) || SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey)) {

            // 另一个[征信录入]状态
            Byte creditRecordStatus = null;
            // 另一个[征信录入]-KEY
            String otherCreditRecordTaskKey = null;

            if (BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey)) {

                creditRecordStatus = loanProcessDO.getSocialCreditRecord();
                otherCreditRecordTaskKey = SOCIAL_CREDIT_RECORD.getCode();

            } else if (SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey)) {

                creditRecordStatus = loanProcessDO.getBankCreditRecord();
                otherCreditRecordTaskKey = BANK_CREDIT_RECORD.getCode();
            }

            // 另一个[征信录入]  ->  未提交
            if (TASK_PROCESS_TODO.equals(creditRecordStatus)) {

                Task bank_social_credit_record_task = taskService.createTaskQuery()
                        .processInstanceId(loanOrderDO.getProcessInstId())
                        .taskDefinitionKey(BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode())
                        .singleResult();

                String rollBackFromTaskKey = null;

                // filter  ->  存在
                if (null != bank_social_credit_record_task) {

                    rollBackFromTaskKey = BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode();
                }

                // filter ->  不存在
                else {

                    rollBackFromTaskKey = otherCreditRecordTaskKey;
                }


                // 被反审的节点列表
                List<String> nextTaskKeys = Lists.newArrayList();

                // 领取校验
                checkTaskDistribution(approval.getOrderId(), nextTaskKeys);

                // 提交校验
                checkTaskProcessStatus(loanProcessDO, nextTaskKeys, taskDefinitionKey);

                // 反审参数
                Map<String, Object> rollBackVariables = Maps.newHashMap();
                rollBackVariables.put(PROCESS_VARIABLE_ACTION, ACTION_ROLL_BACK);
                rollBackVariables.put(PROCESS_VARIABLE_TARGET, taskDefinitionKey);

                // 执行[反审]
                doRollBack(loanOrderDO.getProcessInstId(),
                        Lists.newArrayList(),
                        Lists.newArrayList(),
                        rollBackFromTaskKey,
                        rollBackVariables
                );

                // 反审状态更新
                updateRollBackLoanProcess(approval, nextTaskKeys);
            }

            // 另一个[征信录入]  ->  已提交 || 不存在
            else if (TASK_PROCESS_DONE.equals(creditRecordStatus) || TASK_PROCESS_INIT.equals(creditRecordStatus)) {

                // 被反审的节点列表
                List<String> nextTaskKeys = Lists.newArrayList(
                        LOAN_APPLY.getCode(),
                        VISIT_VERIFY.getCode()
                );

                // 领取校验
                checkTaskDistribution(approval.getOrderId(), nextTaskKeys);

                // 提交校验
                checkTaskProcessStatus(loanProcessDO, nextTaskKeys, taskDefinitionKey);

                // 反审参数
                Map<String, Object> rollBackVariables = Maps.newHashMap();
                rollBackVariables.put(PROCESS_VARIABLE_ACTION, ACTION_ROLL_BACK);
                rollBackVariables.put(PROCESS_VARIABLE_TARGET, taskDefinitionKey);

                // 执行[反审]
                doRollBack(loanOrderDO.getProcessInstId(),
                        Lists.newArrayList(),
                        Lists.newArrayList(VISIT_VERIFY.getCode()),
                        LOAN_APPLY.getCode(),
                        rollBackVariables
                );

                // 反审状态更新
                updateRollBackLoanProcess(approval, nextTaskKeys);
            }

        }

        // [电审]
        else if (TELEPHONE_VERIFY.getCode().equals(taskDefinitionKey)) {

            // 被反审的节点列表
            List<String> nextTaskKeys = Lists.newArrayList(
                    BUSINESS_PAY.getCode(),
                    DATA_FLOW_CONTRACT_P2C.getCode(),
                    VEHICLE_INFORMATION.getCode(),
                    CAR_INSURANCE.getCode(),
                    INSTALL_GPS.getCode(),
                    COMMIT_KEY.getCode()
            );

            // 领取校验
            checkTaskDistribution(approval.getOrderId(), nextTaskKeys);

            // 提交校验
            checkTaskProcessStatus(loanProcessDO, nextTaskKeys, taskDefinitionKey);

            // 执行[反审]
            doRollBack(loanOrderDO.getProcessInstId(),
                    Lists.newArrayList(CAR_INSURANCE.getCode(), INSTALL_GPS.getCode(), COMMIT_KEY.getCode()),
                    Lists.newArrayList(BUSINESS_PAY.getCode(), DATA_FLOW_CONTRACT_P2C.getCode(), VEHICLE_INFORMATION.getCode()),
                    DATA_FLOW_MORTGAGE_P2C_NEW_FILTER.getCode()
            );

            // 反审状态更新
            updateRollBackLoanProcess(approval, nextTaskKeys);
        }

        // [合同套打]
        else if (MATERIAL_PRINT_REVIEW.getCode().equals(approval.getTaskDefinitionKey())) {

            // 被反审的节点列表
            List<String> nextTaskKeys = Lists.newArrayList(
                    MATERIAL_MANAGE.getCode(),
                    APPLY_INSTALMENT.getCode(),
                    DATA_FLOW_CONTRACT_C2B.getCode()
            );

            // 领取校验
            checkTaskDistribution(approval.getOrderId(), nextTaskKeys);

            // 提交校验
            checkTaskProcessStatus(loanProcessDO, nextTaskKeys, approval.getTaskDefinitionKey());

            // 执行[反审]
            doRollBack(loanOrderDO.getProcessInstId(),
                    Lists.newArrayList(MATERIAL_MANAGE.getCode(), APPLY_INSTALMENT.getCode()),
                    Lists.newArrayList(),
                    DATA_FLOW_CONTRACT_C2B.getCode()
            );

            // 反审状态更新
            updateRollBackLoanProcess(approval, nextTaskKeys);
        }

        // 其他节点，暂不支持
        else {

            throw new BizException("[" + LoanProcessEnum.getNameByCode(taskDefinitionKey) + "]暂不支持反审");
        }

        // [领取]完成
        loanProcessApprovalCommonService.finishTask(approval, currentTaskIdList, loanOrderDO.getProcessInstId());

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.getNameByCode(taskDefinitionKey) + "-反审]发起成功");
    }

    /**
     * 节点流程状态校验
     *
     * @param loanProcessDO
     * @param nextTaskKeys          next节点列表
     * @param rollBackOriginTaskKey 发起反审的节点
     */
    private void checkTaskProcessStatus(LoanProcessDO loanProcessDO, List<String> nextTaskKeys, String rollBackOriginTaskKey) {

        Class<LoanProcessDO> clazz = LoanProcessDO.class;

        List<String> taskKeys = Lists.newArrayList(rollBackOriginTaskKey);
        taskKeys.addAll(nextTaskKeys);

        for (int i = 0; i < taskKeys.size(); i++) {

            String taskKey = taskKeys.get(i);

            String[] taskKeyArr = taskKey.split("usertask");

            String methodBody = StringUtil.underline2Camel(taskKeyArr[1]);

            String methodName = "get" + methodBody;

            try {

                Method method = clazz.getMethod(methodName);

                Object result = method.invoke(loanProcessDO);

                // current 节点
                if (rollBackOriginTaskKey.equals(taskKey)) {

                    if (!TASK_PROCESS_DONE.equals(result)) {
                        throw new BizException("操作错误：[" + LoanProcessEnum.getNameByCode(taskKey) + "]未提交，不能发起[反审]");
                    }
                }

                // next 节点
                else {

                    // 1
                    if (TASK_PROCESS_DONE.equals(result)) {

                        throw new BizException("[" + LoanProcessEnum.getNameByCode(taskKey) + "]已提交，无法发起[反审]");
                    }

                    // 0
                    else if (TASK_PROCESS_INIT.equals(result)) {

                        throw new BizException("任务未执行到[" + LoanProcessEnum.getNameByCode(taskKey) + "]，无法发起[反审]");
                    }

                    // 非2/3
                    else if (!TASK_PROCESS_TODO.equals(result) && !TASK_PROCESS_REJECT.equals(result)) {

                        throw new BizException("[" + LoanProcessEnum.getNameByCode(taskKey) + "]流程状态异常，无法发起[反审]");
                    }

                    // 2、3  -> OK
                }

            } catch (NoSuchMethodException e) {
                throw new BizException(e);
            } catch (IllegalAccessException e) {
                throw new BizException(e);
            } catch (InvocationTargetException e) {
                throw new BizException(e);
            }
        }

    }

    /**
     * 任务领取校验
     *
     * @param orderId
     * @param taskKeys
     */
    private void checkTaskDistribution(Long orderId, List<String> taskKeys) {

        if (!CollectionUtils.isEmpty(taskKeys)) {

            taskKeys.stream()
                    .filter(StringUtils::isNotBlank)
                    .forEach(taskKey -> {

                        TaskDistributionDO taskDistributionDO = taskDistributionDOMapper.selectByPrimaryKey(orderId, taskKey);
                        Preconditions.checkArgument(null == taskDistributionDO, "[" + LoanProcessEnum.getNameByCode(taskKey) + "]任务已领取，无法发起[反审]");
                    });
        }

    }

    /**
     * 反审状态更新
     *
     * @param approval
     * @param nextTaskKeys
     */
    private void updateRollBackLoanProcess(ApprovalParam approval, List<String> nextTaskKeys) {

        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(approval.getOrderId());

        loanProcessApprovalCommonService.updateCurrentTaskProcessStatus(loanProcessDO, approval.getTaskDefinitionKey(), TASK_PROCESS_TODO, approval);

        if (!CollectionUtils.isEmpty(nextTaskKeys)) {

            nextTaskKeys.stream()
                    .filter(StringUtils::isNotBlank)
                    .forEach(taskKey -> {

                        loanProcessApprovalCommonService.updateCurrentTaskProcessStatus(loanProcessDO, taskKey, TASK_PROCESS_INIT, approval);
                    });
        }

        loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);
    }

    /**
     * 执行反审  -通用反审参数
     *
     * @param processInstId       流程实例ID
     * @param passTaskKeys        提交节点列表
     * @param cancelTaskKeys      弃单节点列表
     * @param rollBackFromTaskKey 反审起点
     */
    private void doRollBack(String processInstId, List<String> passTaskKeys, List<String> cancelTaskKeys,
                            String rollBackFromTaskKey) {

        // 通用-反审参数
        Map<String, Object> passVariables = Maps.newHashMap();
        passVariables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);

        Map<String, Object> cancelVariables = Maps.newHashMap();
        cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);

        Map<String, Object> rollBackVariables = Maps.newHashMap();
        rollBackVariables.put(PROCESS_VARIABLE_ACTION, ACTION_ROLL_BACK);

        doRollBack(processInstId,
                passTaskKeys, cancelTaskKeys, rollBackFromTaskKey,
                passVariables, cancelVariables, rollBackVariables);
    }

    /**
     * 执行反审  -半通用反审参数     rollBackVariables -> 自定义
     *
     * @param processInstId       流程实例ID
     * @param passTaskKeys        提交节点列表
     * @param cancelTaskKeys      弃单节点列表
     * @param rollBackFromTaskKey 反审起点
     * @param rollBackVariables   反审参数
     */
    private void doRollBack(String processInstId, List<String> passTaskKeys, List<String> cancelTaskKeys,
                            String rollBackFromTaskKey, Map<String, Object> rollBackVariables) {

        // 通用-反审参数
        Map<String, Object> passVariables = Maps.newHashMap();
        passVariables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);

        Map<String, Object> cancelVariables = Maps.newHashMap();
        cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);

        doRollBack(processInstId,
                passTaskKeys, cancelTaskKeys, rollBackFromTaskKey,
                passVariables, cancelVariables, rollBackVariables);
    }

    /**
     * 执行反审  -自定义反审参数
     *
     * @param processInstId       流程实例ID
     * @param passTaskKeys        提交节点列表
     * @param cancelTaskKeys      弃单节点列表
     * @param rollBackFromTaskKey 反审起点
     * @param passVariables       PASS参数
     * @param cancelVariables     CANCEL参数
     * @param rollBackVariables   反审参数
     */
    private void doRollBack(String processInstId,
                            List<String> passTaskKeys,
                            List<String> cancelTaskKeys,
                            String rollBackFromTaskKey,
                            Map<String, Object> passVariables,
                            Map<String, Object> cancelVariables,
                            Map<String, Object> rollBackVariables) {

        List<Task> currentTaskList = loanProcessApprovalCommonService.getCurrentTaskList(processInstId);

        currentTaskList.stream()
                .forEach(task -> {

                    String taskDefinitionKey = task.getTaskDefinitionKey();

                    // PASS
                    if (passTaskKeys.contains(taskDefinitionKey)) {

                        loanProcessApprovalCommonService.completeTask(task.getId(), passVariables);

                    }
                    // CANCEL
                    else if (cancelTaskKeys.contains(taskDefinitionKey)) {

                        loanProcessApprovalCommonService.completeTask(task.getId(), cancelVariables);

                    }
                    // ROLL_BACK
                    else if (rollBackFromTaskKey.equals(taskDefinitionKey)) {

                        loanProcessApprovalCommonService.completeTask(task.getId(), rollBackVariables);
                    }

                });
    }

    /**
     * [业务申请] -> 执行[反审]
     *
     * @param loanOrderDO
     */
    private void doLoanApplyVisitVerifyFilterTask_RollBack(LoanOrderDO loanOrderDO) {

        HashMap<String, Object> passVariables = Maps.newHashMap();
        HashMap<String, Object> cancelVariables = Maps.newHashMap();
        HashMap<String, Object> rollBackVariables = Maps.newHashMap();

        passVariables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);
        cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);
        rollBackVariables.put(PROCESS_VARIABLE_ACTION, ACTION_REJECT_AUTO);
        rollBackVariables.put(PROCESS_VARIABLE_TARGET, StringUtils.EMPTY);


        // getTask -> [上门调查]
        Task visitVerifyTask = loanProcessApprovalCommonService.getTask(loanOrderDO.getProcessInstId(), VISIT_VERIFY.getCode());

        // PASS
        if (null != visitVerifyTask) {
            loanProcessApprovalCommonService.completeTask(visitVerifyTask.getId(), passVariables);
        }


        // 获取当前所有正在执行的任务
        List<Task> currentTaskList = loanProcessApprovalCommonService.getCurrentTaskList(loanOrderDO.getProcessInstId());

        // filterTasks
        if (!CollectionUtils.isEmpty(currentTaskList)) {

            final Task[] filterTask = {null};

            currentTaskList.stream()
                    .filter(Objects::nonNull)
                    .forEach(task -> {

                        if (LOAN_APPLY_VISIT_VERIFY_FILTER.getCode().equals(task.getTaskDefinitionKey())) {

                            if (null == filterTask[0]) {

                                // 仅保留一个 filterTask
                                filterTask[0] = task;

                            } else {
                                // 其他filter"子任务"全部弃掉
                                taskService.complete(task.getId(), cancelVariables);
                            }

                        }

                    });

            Preconditions.checkArgument(ArrayUtils.isNotEmpty(filterTask), "[反审]发起失败");

            // 执行[反审]
            taskService.complete(filterTask[0].getId(), rollBackVariables);
        }

    }

    /**
     * 执行任务
     *
     * @param orderId
     * @param taskDefinitionKey
     * @param action
     */
    private void autoPassTask(Long orderId, String taskDefinitionKey, Byte action) {

        ApprovalParam approvalParam = new ApprovalParam();
        approvalParam.setOrderId(orderId);
        approvalParam.setTaskDefinitionKey(taskDefinitionKey);
        approvalParam.setAction(action);

        approvalParam.setCheckPermission(false);
        approvalParam.setNeedLog(false);
        approvalParam.setNeedPush(false);

        ResultBean<Void> approvalResult = loanProcessService.approval(approvalParam);
        Preconditions.checkArgument(approvalResult.getSuccess(), approvalResult.getMsg());
    }
}
