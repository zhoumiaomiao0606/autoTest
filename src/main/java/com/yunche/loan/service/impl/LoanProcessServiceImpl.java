package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateTimeFormatUtils;
import com.yunche.loan.config.util.DateUtil;
import com.yunche.loan.config.util.EventBusCenter;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.CalMoneyVO;
import com.yunche.loan.domain.vo.LoanProcessLogVO;
import com.yunche.loan.domain.vo.LoanRejectLogVO;
import com.yunche.loan.domain.vo.TaskStateVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static com.yunche.loan.config.constant.ApplyOrderStatusConst.*;
import static com.yunche.loan.config.constant.BankConst.*;
import static com.yunche.loan.config.constant.BaseConst.*;
import static com.yunche.loan.config.constant.CarConst.CAR_KEY_FALSE;
import static com.yunche.loan.config.constant.LoanAmountConst.*;
import static com.yunche.loan.config.constant.LoanCustomerConst.*;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessConst.APPROVAL_NOT_NEED_ORDER_ID_PROCESS_KEYS;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.*;
import static com.yunche.loan.config.constant.LoanRefundApplyConst.REFUND_REASON_3;
import static com.yunche.loan.config.constant.LoanUserGroupConst.*;
import static com.yunche.loan.config.constant.ProcessApprovalConst.*;
import static com.yunche.loan.config.thread.ThreadPool.executorService;
import static com.yunche.loan.service.impl.LoanRejectLogServiceImpl.getTaskStatus;
import static java.util.stream.Collectors.toList;

/**
 * Created by zhouguoliang on 2018/1/30.
 */
@Service
public class LoanProcessServiceImpl implements LoanProcessService {

    private static final Logger logger = LoggerFactory.getLogger(LoanProcessServiceImpl.class);

    /**
     * 换行符
     */
    public static final String NEW_LINE = System.getProperty("line.separator");


    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private LoanCreditInfoDOMapper loanCreditInfoDOMapper;

    @Autowired
    private LoanProcessDOMapper loanProcessDOMapper;

    @Autowired
    private LoanInfoSupplementDOMapper loanInfoSupplementDOMapper;

    @Autowired
    private LoanRefundApplyDOMapper loanRefundApplyDOMapper;

    @Autowired
    private LegworkReimbursementDOMapper legworkReimbursementDOMapper;

    @Autowired
    private LoanProcessLogDOMapper loanProcessLogDOMapper;

    @Autowired
    private LoanRejectLogDOMapper loanRejectLogDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Autowired
    private LoanFinancialPlanTempHisDOMapper loanFinancialPlanTempHisDOMapper;

    @Autowired
    private BankCardRecordDOMapper bankCardRecordDOMapper;

    @Autowired
    private LoanRepayPlanDOMapper loanRepayPlanDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private ActivitiDeploymentMapper activitiDeploymentMapper;

    @Autowired
    private ZhonganInfoDOMapper zhonganInfoDOMapper;

    @Autowired
    private MaterialAuditDOMapper materialAuditDOMapper;

    @Autowired
    private RemitDetailsDOMapper remitDetailsDOMapper;

    @Autowired
    private ConfThirdRealBridgeProcessDOMapper confThirdRealBridgeProcessDOMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private LoanRejectLogService loanRejectLogService;

    @Autowired
    private TaskDistributionService taskDistributionService;

    @Autowired
    private BankSolutionService bankSolutionService;

    @Autowired
    private LoanCustomerService loanCustomerService;

    @Autowired
    private LoanCreditInfoHisService loanCreditInfoHisService;

    @Autowired
    private LoanProcessBridgeService loanProcessBridgeService;

    @Autowired
    private LoanProcessApprovalCommonService loanProcessApprovalCommonService;

    @Autowired
    private LoanProcessApprovalRollBackService loanProcessApprovalRollBackService;

    @Autowired
    private LoanProcessBridgeDOMapper loanProcessBridgeDOMapper;

    @Autowired
    private ThirdPartyFundBusinessDOMapper thirdPartyFundBusinessDOMapper;

    @Autowired
    private ConfThirdPartyMoneyDOMapper confThirdPartyMoneyDOMapper;

    @Autowired
    private BankLendRecordDOMapper bankLendRecordDOMapper;

    @Autowired
    private FinancialProductDOMapper financialProductDOMapper;

    @Autowired
    private ConfLoanApplyDOMapper confLoanApplyDOMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBean<Void> approval(ApprovalParam approval) {
        Preconditions.checkNotNull(approval.getAction(), "审核结果不能为空");
        if (!APPROVAL_NOT_NEED_ORDER_ID_PROCESS_KEYS.contains(approval.getTaskDefinitionKey())) {
            Preconditions.checkNotNull(approval.getOrderId(), "业务单号不能为空");
        }

        // APP通过OrderId弃单
        if (isAppCancelByOrderId(approval)) {
            return execAppCancelByOrderId(approval);
        } else {
            // Web端
            Preconditions.checkArgument(StringUtils.isNotBlank(approval.getTaskDefinitionKey()), "执行任务不能为空");
        }

        // 节点权限校验
        if (approval.isCheckPermission()) {
            permissionService.checkTaskPermission(approval.getTaskDefinitionKey());
        }

        // 【财务报销】
        if (isOutworkerCostApplyTask(approval.getTaskDefinitionKey())) {
            return execOutworkerCostApplyTask(approval);
        }

        // 业务单
        LoanOrderDO loanOrderDO = loanProcessApprovalCommonService.getLoanOrder(approval.getOrderId());

        // 节点实时状态
        LoanProcessDO loanProcessDO = loanProcessApprovalCommonService.getLoanProcess(approval.getOrderId());

        // 贷款基本信息
        LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());

        // 校验审核前提条件
        checkPreCondition(approval.getTaskDefinitionKey(), approval.getAction(), loanOrderDO, loanProcessDO);

        // 日志
        loanProcessApprovalCommonService.log(approval);


        ////////////////////////////////////////// ↓↓↓↓↓ 特殊处理  ↓↓↓↓↓ ////////////////////////////////////////////////

        // 【征信增补】
        if (isCreditSupplementTask(approval.getTaskDefinitionKey(), approval.getAction())) {
            execCreditSupplementTask(approval, loanOrderDO.getProcessInstId(), loanProcessDO);
        }

        // 【资料流转（抵押资料 - 合伙人->公司）】
        if (isDataFlowMortgageP2cNewTask(approval.getTaskDefinitionKey(), approval.getAction())) {
            execDataFlowMortgageP2cNewFilterTask(approval);
        }


        // 【视频面签登记】
        if (isLoanInfoRecordTask(approval)) {
            return execLoanInfoRecordTask(approval, loanProcessDO);
        }

        // 【资料增补】
        if (isInfoSupplementTask(approval)) {
            return execInfoSupplementTask(approval, loanProcessDO);
        }

        // 【金融方案修改申请】
        if (isFinancialSchemeModifyApplyTask(approval.getTaskDefinitionKey())) {
            return execFinancialSchemeModifyApplyTask(approval, loanOrderDO, loanProcessDO);
        }

        // 【退款申请】
        if (isRefundApplyTask(approval.getTaskDefinitionKey())) {
            return execRefundApplyTask(approval, loanOrderDO, loanProcessDO);
        }

        // 【反审】
        if (actionIsRollBack(approval.getAction())) {
            return loanProcessApprovalRollBackService.execRollBackTask(approval, loanOrderDO, loanProcessDO);
        }

        ////////////////////////////////////////// ↑↑↑↑↑ 特殊处理  ↑↑↑↑↑ ////////////////////////////////////////////////


        // 获取当前执行任务（activiti中）
        Task task = loanProcessApprovalCommonService.getTask(loanOrderDO.getProcessInstId(), approval.getTaskDefinitionKey());

        // 先获取提交之前的待执行任务列表
        List<Task> startTaskList = loanProcessApprovalCommonService.getCurrentTaskList(task.getProcessInstanceId());

        // 流程变量
        Map<String, Object> variables = setAndGetVariables(task, approval, loanOrderDO, loanProcessDO);

        // 执行任务
        execTask(task, variables, approval, loanOrderDO, loanProcessDO);

        // 流程数据同步
        syncProcess(startTaskList, loanOrderDO.getProcessInstId(), approval, loanProcessDO, loanBaseInfoDO);

        // 生成客户还款计划
        createRepayPlan(approval.getTaskDefinitionKey(), loanProcessDO, loanOrderDO);

        // [领取]完成
        loanProcessApprovalCommonService.finishTask(approval, getTaskIdList(startTaskList), loanOrderDO.getProcessInstId());

        // 异步打包文件
        asyncPackZipFile(approval.getTaskDefinitionKey(), approval.getAction(), loanProcessDO, 2);

        // 执行当前节点-附带任务
        doCurrentNodeAttachTask(approval, loanOrderDO, loanProcessDO);

        // 异步推送
        loanProcessApprovalCommonService.asyncPush(loanOrderDO, approval);

        // 如果是打款确认 和 代偿确认 则通知财务系统
        /*EventBusCenter.eventBus.post(approval);*/

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.getNameByCode(approval.getOriginalTaskDefinitionKey()) + "]任务执行成功");
    }

    /**
     * 是否为：[贷款信息登记]任务
     *
     * @param approval
     * @return
     */
    private boolean isLoanInfoRecordTask(ApprovalParam approval) {

        boolean isLoanInfoRecordTask = LOAN_INFO_RECORD.getCode().equals(approval.getTaskDefinitionKey());

        return isLoanInfoRecordTask;
    }

    /**
     * 执行 - [视频面签登记]任务
     *
     * @param approval
     * @param currentLoanProcessDO
     * @return
     */
    private ResultBean<Void> execLoanInfoRecordTask(ApprovalParam approval, LoanProcessDO currentLoanProcessDO) {

        Byte loanInfoRecordStatus = currentLoanProcessDO.getLoanInfoRecord();

        Byte action = approval.getAction();

        // pass
        if (ACTION_PASS.equals(action)) {

            // task_process -> 0   ==> 任务不存在
            if (TASK_PROCESS_INIT.equals(loanInfoRecordStatus)) {
                throw new BizException("当前任务不存在");
            }

            // task_process -> 1   ==> 任务已提交
            else if (TASK_PROCESS_DONE.equals(loanInfoRecordStatus)) {

                throw new BizException("当前任务已提交，请勿重复审核！");
            }

            // task_process -> 2   ==> 任务未提交
            else if (TASK_PROCESS_TODO.equals(loanInfoRecordStatus)) {

                LoanProcessDO loanProcessDO = new LoanProcessDO();
                loanProcessDO.setOrderId(currentLoanProcessDO.getOrderId());
                // done
                loanProcessDO.setLoanInfoRecord(TASK_PROCESS_DONE);

                loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);
            }

            // task_process -> other   ==> 任务状态异常
            else {

                throw new BizException("当前任务状态异常");
            }


        } else {

            // action只能为：PASS
            throw new BizException("流程审核参数有误");
        }

        return ResultBean.ofSuccess(null, "[贷款信息登记]任务执行成功");
    }

    /**
     * 反审
     *
     * @param action
     * @return
     */
    private boolean actionIsRollBack(Byte action) {
        return ACTION_ROLL_BACK.equals(action);
    }

    /**
     * [领取]完成
     *
     * @param approval
     */
    private void finishTask_(ApprovalParam approval) {

        if (null != approval.getTaskId()) {

            // PASS
            if (ACTION_PASS.equals(approval.getAction())) {
                taskDistributionService.finish(approval.getTaskId(), approval.getOrderId(), approval.getTaskDefinitionKey());
            }
        }
    }


    /**
     * 生成客户还款计划
     *
     * @param taskDefinitionKey
     * @param loanProcessDO
     * @param loanOrderDO
     */
    private void createRepayPlan(String taskDefinitionKey, LoanProcessDO loanProcessDO, LoanOrderDO loanOrderDO) {

        if (BANK_CARD_RECORD.getCode().equals(taskDefinitionKey) && ACTION_PASS.equals(loanProcessDO.getTelephoneVerify())) {

            // AUTO_PASS[客户还款计划]
            autoCompleteTask(loanOrderDO.getProcessInstId(), loanProcessDO.getOrderId(), CUSTOMER_REPAY_PLAN.getCode());

            // 贷款期数
            LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
            Long bankCardRecordId = loanOrderDO.getBankCardRecordId();
            BigDecimal eachMonthRepay = loanFinancialPlanDO.getEachMonthRepay();
            BankCardRecordDO bankCardRecordDO = bankCardRecordDOMapper.selectByPrimaryKey(bankCardRecordId);
            Integer loanTime = loanFinancialPlanDO.getLoanTime();// 贷款期数
            Date firstRepaymentDate = bankCardRecordDO.getFirstRepaymentDate();// 首月还款日

            // 插入客户还款计划
            insertRepayPlan(loanOrderDO.getId(), firstRepaymentDate, loanTime, eachMonthRepay);
        }
    }

    /**
     * 插入客户还款计划
     *
     * @param orderId
     * @param firstRepaymentDate
     * @param loanTime
     */
    private void insertRepayPlan(Long orderId, Date firstRepaymentDate, Integer loanTime, BigDecimal eachMonthRepay) {

        for (int i = 0; i < loanTime; i++) {
            LoanRepayPlanDO loanRepayPlanDO = new LoanRepayPlanDO();
            loanRepayPlanDO.setOrderId(orderId);
            loanRepayPlanDO.setIsOverdue(K_YORN_NO);
            loanRepayPlanDO.setPayableAmount(eachMonthRepay);
            loanRepayPlanDO.setStatus(TASK_PROCESS_DONE);
            loanRepayPlanDO.setNper(i + 1);
            loanRepayPlanDO.setOverdueAmount(new BigDecimal(0));
            loanRepayPlanDO.setActualRepayAmount(new BigDecimal(0));
            if (0 == i) {
                loanRepayPlanDO.setRepayDate(firstRepaymentDate);
            } else {
                Calendar rightNow = Calendar.getInstance();
                rightNow.setTime(firstRepaymentDate);
                rightNow.add(Calendar.MONTH, i);// 日期加
                Date repayDate = rightNow.getTime();
                loanRepayPlanDO.setRepayDate(repayDate);
            }
            loanRepayPlanDOMapper.insert(loanRepayPlanDO);
        }

    }

    /**
     * 异步打包文件
     *
     * @param taskDefinitionKey
     * @param action
     * @param loanProcessDO
     * @param retryNum          重试次数
     */
    private void asyncPackZipFile(String taskDefinitionKey, Byte action, LoanProcessDO loanProcessDO, Integer retryNum) {

        if (null == retryNum) {
            retryNum = 0;
        }
        // 资料增补、电审、录入提车资料 - PASS 后，都会出发异步打包操作
        boolean bool = (VEHICLE_INFORMATION.getCode().equals(taskDefinitionKey)
                || INFO_SUPPLEMENT.getCode().equals(taskDefinitionKey)
                || LOAN_APPLY.getCode().equals(taskDefinitionKey)
                || VISIT_VERIFY.getCode().equals(taskDefinitionKey)
        ) && ACTION_PASS.equals(action);

        if (bool) {

            if (retryNum < 0) {
                return;
            }
            retryNum--;

            int finalRetryNum = retryNum;
            executorService.execute(() -> {

                ResultBean<String> resultBean = null;
                try {
                    // 打包，并上传至OSS
                    resultBean = materialService.downloadFiles2OSS(loanProcessDO.getOrderId(), true);
                } catch (Exception e) {
                    logger.error("asyncPackZipFile error", e);
                } finally {
                    // 失败，重试
                    if (null == resultBean || !resultBean.getSuccess()) {
                        asyncPackZipFile(taskDefinitionKey, action, loanProcessDO, finalRetryNum);
                    }
                }
            });
        }
    }

    /**
     * 【金融方案修改申请 || 审核】任务
     *
     * @param taskDefinitionKey
     * @return
     */
    private boolean isFinancialSchemeModifyApplyTask(String taskDefinitionKey) {
        boolean isFinancialSchemeModifyApplyTask = FINANCIAL_SCHEME_MODIFY_APPLY.getCode().equals(taskDefinitionKey)
                || FINANCIAL_SCHEME_MODIFY_APPLY_REVIEW.getCode().equals(taskDefinitionKey);
        return isFinancialSchemeModifyApplyTask;
    }

    /**
     * 【退款申请】任务
     *
     * @param taskDefinitionKey
     * @return
     */
    private boolean isRefundApplyTask(String taskDefinitionKey) {
        boolean isRefundApplyTask = REFUND_APPLY.getCode().equals(taskDefinitionKey)
                || REFUND_APPLY_REVIEW.getCode().equals(taskDefinitionKey);
        return isRefundApplyTask;
    }

    /**
     * 【财务报销】任务
     *
     * @param taskDefinitionKey
     * @return
     */
    private boolean isOutworkerCostApplyTask(String taskDefinitionKey) {
        boolean isRefundApplyTask = OUTWORKER_COST_APPLY.getCode().equals(taskDefinitionKey)
                || OUTWORKER_COST_APPLY_REVIEW.getCode().equals(taskDefinitionKey);
        return isRefundApplyTask;
    }

    /**
     * 【资料流转（抵押资料 - 合伙人->公司）】任务 -[新建]
     *
     * @param taskDefinitionKey
     * @param action
     * @return
     */
    private boolean isDataFlowMortgageP2cNewTask(String taskDefinitionKey, Byte action) {
        // 新建【资料流转（抵押资料 - 合伙人->公司）】单据
        boolean isDataFlowMortgageP2cNewTask = DATA_FLOW_MORTGAGE_P2C.getCode().equals(taskDefinitionKey)
                && ACTION_NEW_TASK.equals(action);
        return isDataFlowMortgageP2cNewTask;
    }

    /**
     * 执行 -【金融方案修改申请】
     * <p>
     * 能发起 - 【金融方案修改申请】的节点：
     * [业务付款申请]-未提交 || [业务审批]-未提交  ||  [放款审批]-未提交        || [放款确认]-已退款
     *
     * @param approval
     * @param loanOrderDO
     * @param loanProcessDO
     * @return
     */
    private ResultBean<Void> execFinancialSchemeModifyApplyTask(ApprovalParam approval, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        String taskDefinitionKey = approval.getTaskDefinitionKey();

        // 【金融方案修改申请】
        if (FINANCIAL_SCHEME_MODIFY_APPLY.getCode().equals(taskDefinitionKey)) {

            // 提交
            Preconditions.checkArgument(ACTION_PASS.equals(approval.getAction()), "流程审核参数有误");

            // 操作锁定 (退款申请中/金融方案修改申请中);
            lockProcess(loanProcessDO);

            // 更新申请单状态
            updateFinancialSchemeModifyApply(approval, APPLY_ORDER_TODO);

            // [领取]完成
            finishTask_(approval);
        }

        // 【金融方案修改申请审核】
        else if (FINANCIAL_SCHEME_MODIFY_APPLY_REVIEW.getCode().equals(taskDefinitionKey)) {

            // 通过/打回/弃单(整个流程)
            if (ACTION_PASS.equals(approval.getAction())) {

                execFinancialSchemeModifyApplyReviewTask(approval, loanOrderDO.getLoanFinancialPlanId(), loanProcessDO);

            } else if (ACTION_REJECT_MANUAL.equals(approval.getAction())) {

                // 更新申请单状态
                updateFinancialSchemeModifyApply(approval, APPLY_ORDER_REJECT);

                // 打回记录
                createRejectLog_(loanProcessDO.getOrderId(), approval.getTaskDefinitionKey(), FINANCIAL_SCHEME_MODIFY_APPLY.getCode(), approval.getInfo());

            } else if (ACTION_CANCEL.equals(approval.getAction())) {

                // 更新申请单状态
                updateFinancialSchemeModifyApply(approval, APPLY_ORDER_CANCEL);

                // 结束流程
                dealCancelTask(loanOrderDO.getProcessInstId());

                // 更新流程状态
                loanProcessDO.setOrderStatus(ORDER_STATUS_CANCEL);
                loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);

            } else {

                throw new BizException("流程审核参数有误");
            }

            // [领取]完成
            finishTask_(approval);

        } else {

            throw new BizException("流程审核参数有误");
        }

        // 额外任务
        doCurrentNodeAttachTask(approval, loanOrderDO, loanProcessDO);

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.getNameByCode(taskDefinitionKey) + "]任务执行成功");
    }

    /**
     * 锁定操作(退款申请中/金融方案修改申请中)
     * <p>
     * 锁定操作 ===>  [打款确认 - REMIT_REVIEW] --> 22
     *
     * @param loanProcessDO
     */
    private void lockProcess(LoanProcessDO loanProcessDO) {
        if (TASK_PROCESS_TODO.equals(loanProcessDO.getBusinessReview())) {
            loanProcessDO.setRemitReview(TASK_PROCESS_LOCKED);
        } else if (TASK_PROCESS_TODO.equals(loanProcessDO.getLoanReview())) {
            loanProcessDO.setRemitReview(TASK_PROCESS_LOCKED);
        }
        loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);
    }

    /**
     * 打回记录
     *
     * @param orderId
     * @param rejectOriginTask
     * @param rejectToTask
     * @param reason
     */
    private void createRejectLog_(Long orderId, String rejectOriginTask, String rejectToTask, String reason) {

        LoanRejectLogDO loanRejectLogDO = new LoanRejectLogDO();
        loanRejectLogDO.setOrderId(orderId);
        loanRejectLogDO.setRejectOriginTask(rejectOriginTask);
        loanRejectLogDO.setRejectToTask(rejectToTask);
        loanRejectLogDO.setReason(reason);
        loanRejectLogDO.setGmtCreate(new Date());

        int count = loanRejectLogDOMapper.insertSelective(loanRejectLogDO);
        Preconditions.checkArgument(count > 0, "打回记录失败");
    }

    /**
     * 金融方案修改审核 -审核通过
     *
     * @param approval
     * @param loanFinancialPlanId
     * @param loanProcessDO
     */
    private void execFinancialSchemeModifyApplyReviewTask(ApprovalParam approval, Long loanFinancialPlanId, LoanProcessDO loanProcessDO) {

        // 角色
        Set<String> userGroupNameSet = permissionService.getLoginUserHasUserGroups();
        // 最大电审角色等级
        Byte maxRoleLevel = getTelephoneVerifyMaxRole(userGroupNameSet);
        // 电审专员及以上有权电审
        Preconditions.checkArgument(null != maxRoleLevel && maxRoleLevel >= LEVEL_TELEPHONE_VERIFY_COMMISSIONER, "您无[电审]权限");

        // 获取贷款额度
        LoanFinancialPlanTempHisDO loanFinancialPlanTempHisDO = loanFinancialPlanTempHisDOMapper.selectByPrimaryKey(approval.getSupplementOrderId());
        Preconditions.checkArgument(null != loanFinancialPlanTempHisDO && null != loanFinancialPlanTempHisDO.getFinancial_loan_amount(), "贷款额不能为空");
        double loanAmount = loanFinancialPlanTempHisDO.getFinancial_loan_amount().doubleValue();

        // 直接通过
        if (loanAmount >= 0 && loanAmount <= 100000) {
            // 完成任务：全部角色直接过单
            passFinancialSchemeModifyApplyReviewTask(approval, APPLY_ORDER_PASS, loanFinancialPlanId, loanProcessDO);
        } else if (loanAmount > 100000 && loanAmount <= 300000) {
            // 电审主管以上可过单
            if (maxRoleLevel < LEVEL_TELEPHONE_VERIFY_LEADER) {
                // 记录
                updateFinancialSchemeModifyApply(approval, maxRoleLevel);
            } else {
                // 完成任务
                passFinancialSchemeModifyApplyReviewTask(approval, APPLY_ORDER_PASS, loanFinancialPlanId, loanProcessDO);
            }
        } else if (loanAmount > 300000 && loanAmount <= 500000) {
            // 电审经理以上可过单
            if (maxRoleLevel < LEVEL_TELEPHONE_VERIFY_MANAGER) {
                // 记录
                updateFinancialSchemeModifyApply(approval, maxRoleLevel);
            } else {
                // 完成任务
                passFinancialSchemeModifyApplyReviewTask(approval, APPLY_ORDER_PASS, loanFinancialPlanId, loanProcessDO);
            }
        } else if (loanAmount > 500000) {
            // 总监以上可过单
            if (maxRoleLevel < LEVEL_DIRECTOR) {
                // 记录
                updateFinancialSchemeModifyApply(approval, maxRoleLevel);
            } else {
                // 完成任务
                passFinancialSchemeModifyApplyReviewTask(approval, APPLY_ORDER_PASS, loanFinancialPlanId, loanProcessDO);
            }
        }
    }

    /**
     * 通过[金融方案修改申请单]
     *
     * @param approval
     * @param applyOrderStatus
     * @param loanFinancialPlanId
     * @param loanProcessDO
     */
    private void passFinancialSchemeModifyApplyReviewTask(ApprovalParam approval, Byte applyOrderStatus, Long loanFinancialPlanId, LoanProcessDO loanProcessDO) {

        // 修改申请单状态
        updateFinancialSchemeModifyApply(approval, applyOrderStatus);

        // 更新金融方案原表数据
        updateFinancialPlan(loanFinancialPlanId, approval.getSupplementOrderId());

        // 自动打回 ->【业务付款】 （重走【业务付款】）
        autoReject2BusinessPay_passFinancialSchemeModifyApplyReviewTask(loanProcessDO);
    }

    /**
     * 自动打回 ->【业务付款】 （重走【业务付款】）
     *
     * @param loanProcessDO
     */
    private void autoReject2BusinessPay_passFinancialSchemeModifyApplyReviewTask(LoanProcessDO loanProcessDO) {
        if (TASK_PROCESS_TODO.equals(loanProcessDO.getBusinessPay()) || TASK_PROCESS_REJECT.equals(loanProcessDO.getBusinessPay())) {
            return;
        }
        // BUSINESS_REVIEW -> [BUSINESS_PAY]
        if (TASK_PROCESS_TODO.equals(loanProcessDO.getBusinessReview())) {
            autoReject2BusinessPay(loanProcessDO.getOrderId(), BUSINESS_REVIEW.getCode(), loanProcessDO);
        }
        // LOAN_REVIEW -> [BUSINESS_PAY]
        else if (TASK_PROCESS_TODO.equals(loanProcessDO.getLoanReview())) {
            autoReject2BusinessPay(loanProcessDO.getOrderId(), LOAN_REVIEW.getCode(), loanProcessDO);
        }
        // REMIT_REVIEW_FILTER -> [BUSINESS_PAY]
        else if (TASK_PROCESS_REFUND.equals(loanProcessDO.getRemitReview())) {
            autoReject2BusinessPay(loanProcessDO.getOrderId(), REMIT_REVIEW_FILTER.getCode(), loanProcessDO);
        }
    }

    /**
     * 自动打回 ->【业务付款】 （重走【业务付款】）
     *
     * @param orderId
     * @param autoRejectOriginTaskDefinitionKey (打回)源节点
     * @param loanProcessDO
     */
    private void autoReject2BusinessPay(Long orderId, String autoRejectOriginTaskDefinitionKey, LoanProcessDO loanProcessDO) {

        ApprovalParam approvalParam = new ApprovalParam();

        approvalParam.setOrderId(orderId);
        approvalParam.setTaskDefinitionKey(autoRejectOriginTaskDefinitionKey);
        approvalParam.setAction(ACTION_REJECT_AUTO);

        approvalParam.setCheckPermission(false);
        approvalParam.setNeedLog(false);
        approvalParam.setNeedPush(false);

        approval(approvalParam);

        loanProcessDO.setBusinessPay(TASK_PROCESS_TODO);
        loanProcessDO.setBusinessReview(TASK_PROCESS_INIT);
        loanProcessDO.setLoanReview(TASK_PROCESS_INIT);
        loanProcessDO.setRemitReview(TASK_PROCESS_INIT);
        loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);
    }

    /**
     * 更新金融方案原表数据
     *
     * @param loanFinancialPlanId
     * @param hisId
     */
    private void updateFinancialPlan(Long loanFinancialPlanId, Long hisId) {

        LoanFinancialPlanTempHisDO loanFinancialPlanTempHisDO = loanFinancialPlanTempHisDOMapper.selectByPrimaryKey(hisId);
        Preconditions.checkNotNull(loanFinancialPlanTempHisDO, "申请单ID有误");

        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        loanFinancialPlanDO.setId(loanFinancialPlanId);
        loanFinancialPlanDO.setFinancialProductId(loanFinancialPlanTempHisDO.getFinancial_product_id());
        loanFinancialPlanDO.setAppraisal(loanFinancialPlanTempHisDO.getFinancial_appraisal());
        loanFinancialPlanDO.setBank(loanFinancialPlanTempHisDO.getFinancial_bank());
        loanFinancialPlanDO.setLoanTime(loanFinancialPlanTempHisDO.getFinancial_loan_time());
        loanFinancialPlanDO.setDownPaymentRatio(loanFinancialPlanTempHisDO.getFinancial_down_payment_ratio());
        loanFinancialPlanDO.setFinancialProductName(loanFinancialPlanTempHisDO.getFinancial_product_name());
        loanFinancialPlanDO.setSignRate(loanFinancialPlanTempHisDO.getFinancial_sign_rate());
        loanFinancialPlanDO.setLoanAmount(loanFinancialPlanTempHisDO.getFinancial_loan_amount());
        loanFinancialPlanDO.setFirstMonthRepay(loanFinancialPlanTempHisDO.getFinancial_first_month_repay());
        loanFinancialPlanDO.setCarPrice(loanFinancialPlanTempHisDO.getFinancial_car_price());
        loanFinancialPlanDO.setDownPaymentMoney(loanFinancialPlanTempHisDO.getFinancial_down_payment_money());
        loanFinancialPlanDO.setBankPeriodPrincipal(loanFinancialPlanTempHisDO.getFinancial_bank_period_principal());
        loanFinancialPlanDO.setEachMonthRepay(loanFinancialPlanTempHisDO.getFinancial_each_month_repay());
        loanFinancialPlanDO.setPrincipalInterestSum(loanFinancialPlanTempHisDO.getFinancial_total_repayment_amount());

        loanFinancialPlanDO.setGmtModify(new Date());

        int count = loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
        Preconditions.checkArgument(count > 0, "审核失败");
    }

    /**
     * 更新申请单状态
     *
     * @param approval
     * @param applyOrderStatus
     */
    private void updateFinancialSchemeModifyApply(ApprovalParam approval, Byte applyOrderStatus) {
        Preconditions.checkNotNull(approval.getSupplementOrderId(), "[申请单ID]不能为空");

        // update
        LoanFinancialPlanTempHisDO loanFinancialPlanTempHisDO = new LoanFinancialPlanTempHisDO();
        loanFinancialPlanTempHisDO.setId(approval.getSupplementOrderId());
        loanFinancialPlanTempHisDO.setStatus(applyOrderStatus);

        EmployeeDO loginUser = SessionUtils.getLoginUser();
        if (FINANCIAL_SCHEME_MODIFY_APPLY.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {
            loanFinancialPlanTempHisDO.setInitiator_id(loginUser.getId());
            loanFinancialPlanTempHisDO.setInitiator_name(loginUser.getName());
        } else if (FINANCIAL_SCHEME_MODIFY_APPLY_REVIEW.getCode().equals(approval.getTaskDefinitionKey())) {
            loanFinancialPlanTempHisDO.setAuditor_id(loginUser.getId());
            loanFinancialPlanTempHisDO.setAuditor_name(loginUser.getName());
            if (ACTION_PASS.equals(approval.getAction())) {
                loanFinancialPlanTempHisDO.setEnd_time(new Date());
            }
        }

        int count = loanFinancialPlanTempHisDOMapper.updateByPrimaryKeySelective(loanFinancialPlanTempHisDO);
        Preconditions.checkArgument(count > 0, "失败");
    }

    /**
     * 执行 -【退款申请】
     * <p>
     * 能发起[退款申请]的节点：[打款确认]-已提交
     *
     * @param approval
     * @param loanOrderDO
     * @param loanProcessDO
     * @return
     */
    private ResultBean<Void> execRefundApplyTask(ApprovalParam approval, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        // 先获取提交之前的待执行任务ID列表
        List<String> currentTaskIdList = loanProcessApprovalCommonService.getCurrentTaskIdList(loanOrderDO.getProcessInstId());

        String taskDefinitionKey = approval.getTaskDefinitionKey();

        // 【退款申请】
        if (REFUND_APPLY.getCode().equals(taskDefinitionKey)) {
            // 提交
            Preconditions.checkArgument(ACTION_PASS.equals(approval.getAction()), "流程审核参数有误");

            // check
            Preconditions.checkArgument(!TASK_PROCESS_INIT.equals(loanProcessDO.getLoanReview()), "[打款确认]未提交，无法发起退款申请");
            Preconditions.checkArgument(!TASK_PROCESS_TODO.equals(loanProcessDO.getLoanReview()), "[打款确认]未提交，无法发起退款申请");

            // 锁定操作 (退款申请中/金融方案修改申请中)
            lockProcess(loanProcessDO);

            // 更新申请单状态
            updateRefundApply(approval, APPLY_ORDER_TODO);
        }

        // 【退款申请审核】
        else if (REFUND_APPLY_REVIEW.getCode().equals(taskDefinitionKey)) {

            // 通过/打回
            if (ACTION_PASS.equals(approval.getAction())) {

                // 退款原因(类型)：3-业务审批重审     ===>   自动打回 ->【业务付款】
                LoanRefundApplyDO loanRefundApplyDO = getLoanRefundApply(approval.getOrderId());
                if (REFUND_REASON_3.equals(loanRefundApplyDO.getRefund_reason())) {

                    // 自动打回   [退款申请-已提交] ->【业务付款】 （重走【业务付款】）
                    if (TASK_PROCESS_DONE.equals(loanProcessDO.getRemitReview())) {

                        // 能发起[退款申请]的节点：[打款确认]-已提交
                        autoReject2BusinessPay(loanProcessDO.getOrderId(), REMIT_REVIEW_FILTER.getCode(), loanProcessDO);
                    }
                }

                // 更新申请单状态
                updateRefundApply(approval, APPLY_ORDER_PASS);

                // 更新流程（已退款）
                loanProcessDO.setRemitReview(TASK_PROCESS_REFUND);
                loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);

                // 异步同步财务数据
               /* EventBusCenter.eventBus.post(approval);*/

            } else if (ACTION_REJECT_MANUAL.equals(approval.getAction())) {

                // 更新申请单状态
                updateRefundApply(approval, APPLY_ORDER_REJECT);

                // 打回记录
                createRejectLog_(loanProcessDO.getOrderId(), taskDefinitionKey, REFUND_APPLY.getCode(), approval.getInfo());

            } else {

                throw new BizException("流程审核参数有误");
            }

        } else {

            throw new BizException("流程审核参数有误");
        }

        loanProcessApprovalCommonService.finishTask(approval, currentTaskIdList, loanOrderDO.getProcessInstId());

        return ResultBean.ofSuccess(null, "[" + LoanProcessEnum.getNameByCode(taskDefinitionKey) + "]任务执行成功");
    }

    /**
     * 执行 -【财务报销】
     *
     * @param approval
     * @return
     */
    private ResultBean<Void> execOutworkerCostApplyTask(ApprovalParam approval) {
        Preconditions.checkNotNull(approval.getSupplementOrderId(), "财务报销单ID不能为空");

        // [外勤费用申报]
        if (OUTWORKER_COST_APPLY.getCode().equals(approval.getTaskDefinitionKey())) {

            // PASS
            if (ACTION_PASS.equals(approval.getAction())) {

                updateOutworkerCostApplyProcess(approval, ApplyOrderStatus.APPLY_ORDER_DONE__APPLY_ORDER_REVIEW_TODO);

                return ResultBean.ofSuccess(null, "[外勤费用申报]任务执行成功");
            }
        }

        // [财务报销]
        else if (OUTWORKER_COST_APPLY_REVIEW.getCode().equals(approval.getTaskDefinitionKey())) {

            Byte action = approval.getAction();

            // PASS
            if (ACTION_PASS.equals(action)) {

                updateOutworkerCostApplyProcess(approval, ApplyOrderStatus.APPLY_ORDER_REVIEW_PASS);

                return ResultBean.ofSuccess(null, "[财务报销]任务执行成功");
            }
            // REJECT
            else if (ACTION_REJECT_MANUAL.equals(action)) {

                updateOutworkerCostApplyProcess(approval, ApplyOrderStatus.APPLY_ORDER_REJECT);

                return ResultBean.ofSuccess(null, "[财务报销]任务执行成功");
            }
        }

        return ResultBean.ofError("流程审核参数有误");
    }

    private void updateOutworkerCostApplyProcess(ApprovalParam approval, Byte applyOrderStatus) {

        LegworkReimbursementDO legworkReimbursementDO = new LegworkReimbursementDO();
        legworkReimbursementDO.setId(approval.getSupplementOrderId());

        legworkReimbursementDO.setStatus(applyOrderStatus);


        EmployeeDO loginUser = SessionUtils.getLoginUser();
        legworkReimbursementDO.setApplyUserId(loginUser.getId());
        legworkReimbursementDO.setApplyUserName(loginUser.getName());

        legworkReimbursementDO.setReviewUserId(loginUser.getId());
        legworkReimbursementDO.setReviewUserName(loginUser.getName());

        legworkReimbursementDO.setGmtUpdateTime(new Date());

        int count = legworkReimbursementDOMapper.updateByPrimaryKeySelective(legworkReimbursementDO);
        Preconditions.checkArgument(count > 0, "更新失败");
    }

    /**
     * 更新退款申请单状态
     *
     * @param approval
     * @param applyOrderStatus
     */
    private void updateRefundApply(ApprovalParam approval, Byte applyOrderStatus) {
        Preconditions.checkNotNull(approval.getSupplementOrderId(), "[申请单ID]不能为空");

        // update
        LoanRefundApplyDO loanRefundApplyDO = new LoanRefundApplyDO();
        loanRefundApplyDO.setId(approval.getSupplementOrderId());
        loanRefundApplyDO.setStatus(applyOrderStatus);

        EmployeeDO loginUser = SessionUtils.getLoginUser();
        if (REFUND_APPLY.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {
            loanRefundApplyDO.setInitiator_id(loginUser.getId());
            loanRefundApplyDO.setInitiator_name(loginUser.getName());
        } else if (REFUND_APPLY_REVIEW.getCode().equals(approval.getTaskDefinitionKey())) {
            loanRefundApplyDO.setAuditor_id(loginUser.getId());
            loanRefundApplyDO.setAuditor_name(loginUser.getName());
            if (ACTION_PASS.equals(approval.getAction())) {
                loanRefundApplyDO.setEnd_time(new Date());
            }
        }

        int count = loanRefundApplyDOMapper.updateByPrimaryKeySelective(loanRefundApplyDO);
        Preconditions.checkArgument(count > 0, "失败");
    }

    /**
     * 流程数据同步： 同步activiti与本地流程数据
     *
     * @param startTaskList        起始任务列表
     * @param processInstId
     * @param approval
     * @param currentLoanProcessDO
     * @param loanBaseInfoDO
     */
    private void syncProcess(List<Task> startTaskList, String processInstId, ApprovalParam approval,
                             LoanProcessDO currentLoanProcessDO, LoanBaseInfoDO loanBaseInfoDO) {

        // 更新状态
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(approval.getOrderId());

        // 如果弃单，则记录弃单节点
        if (ACTION_CANCEL.equals(approval.getAction())) {
            loanProcessDO.setOrderStatus(ORDER_STATUS_CANCEL);
            loanProcessDO.setCancelTaskDefKey(approval.getTaskDefinitionKey());
            loanProcessApprovalCommonService.updateCurrentTaskProcessStatus(loanProcessDO, approval.getTaskDefinitionKey(), TASK_PROCESS_CANCEL, approval);
        }

        // 更新当前执行的任务状态
        Byte taskProcessStatus = null;
        if (ACTION_REJECT_MANUAL.equals(approval.getAction()) || ACTION_REJECT_AUTO.equals(approval.getAction())) {
            taskProcessStatus = TASK_PROCESS_INIT;
        } else if (ACTION_PASS.equals(approval.getAction()) && !TELEPHONE_VERIFY.getCode().equals(approval.getTaskDefinitionKey())) {
            // Tips：[电审]通过 状态更新不走这里
            taskProcessStatus = TASK_PROCESS_DONE;
        }
        loanProcessApprovalCommonService.updateCurrentTaskProcessStatus(loanProcessDO, approval.getTaskDefinitionKey(), taskProcessStatus, approval);

        // 更新新产生的任务状态
        updateNextTaskProcessStatus(loanProcessDO, processInstId, startTaskList, approval, currentLoanProcessDO);

        // 特殊处理：部分节点的同步  !!!
        special_syncProcess(approval, loanProcessDO, currentLoanProcessDO, loanBaseInfoDO);

        // 更新本地流程记录
        loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);
    }

    /**
     * 特殊处理：部分节点的同步
     *
     * @param approval
     * @param loanProcessDO
     * @param currentLoanProcessDO
     * @param loanBaseInfoDO
     */
    private void special_syncProcess(ApprovalParam approval, LoanProcessDO loanProcessDO,
                                     LoanProcessDO currentLoanProcessDO, LoanBaseInfoDO loanBaseInfoDO) {

        // [征信申请] - PASS   ==>  [贷款信息登记] task_process 特殊处理
        boolean is_credit_apply_task__and__action_pass = is_credit_apply_task_pass(approval);

        if (is_credit_apply_task__and__action_pass) {

            // 中国工商银行杭州城站支行 || 哈尔滨顾乡支行 || 台州支行 || 测试银行
            if (BANK_NAME_ICBC_HangZhou_City_Station_Branch.equals(loanBaseInfoDO.getBank())
                    || BANK_NAME_ICBC_Harbin_GuXiang_Branch.equals(loanBaseInfoDO.getBank())
                    || BANK_NAME_ICBC_TaiZhou_LuQiao_Branch.equals(loanBaseInfoDO.getBank())
                    || BANK_NAME_ICBC_TaiZhou_LuQiao_Branch_TEST.equals(loanBaseInfoDO.getBank())) {

                // [贷款信息登记] 是否已存在
                Byte loanInfoRecordStatus = currentLoanProcessDO.getLoanInfoRecord();

                // 不存在
                if (TASK_PROCESS_INIT.equals(loanInfoRecordStatus)) {

                    // 0-未到此   --> 即为：第一次生成
                    loanProcessDO.setLoanInfoRecord(TASK_PROCESS_TODO);

                } else {

                    // 已存在  -> 存在： 2-未提交 / 1-已提交

                    // nothing
                }
            }

        }

        // [电审]打回  -[银行开卡]不打回
//        else if () {
//
//        }
    }

    /**
     * [征信申请] - PASS
     *
     * @param approval
     * @return
     */
    private boolean is_credit_apply_task_pass(ApprovalParam approval) {

        // credit_apply   &&   pass
        boolean is_credit_apply_task__and__action_pass = CREDIT_APPLY.getCode().equals(approval.getTaskDefinitionKey())
                && ACTION_PASS.equals(approval.getAction());

        return is_credit_apply_task__and__action_pass;
    }

    /**
     * 是否为：APP通过OrderId弃单
     *
     * @param approval
     * @return
     */
    private boolean isAppCancelByOrderId(ApprovalParam approval) {
        boolean isAppCancelByOrderId = approval.getCancelByOrderId() && ACTION_CANCEL.equals(approval.getAction());
        // 节点标记
        if (isAppCancelByOrderId) {
            approval.setTaskDefinitionKey("APP-CANCEL");
        }
        return isAppCancelByOrderId;
    }

    /**
     * 执行：APP通过OrderId弃单
     *
     * @param approval
     */
    private ResultBean<Void> execAppCancelByOrderId(ApprovalParam approval) {

        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(approval.getOrderId());
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

        // 进行中 + 未打款确认
        boolean notRemitReview = ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus()) && !TASK_PROCESS_DONE.equals(loanProcessDO.getRemitReview());
        Preconditions.checkArgument(notRemitReview, "订单已放款，无法弃单！");

        // 业务单
        LoanOrderDO loanOrderDO = loanProcessApprovalCommonService.getLoanOrder(approval.getOrderId());

        // 日志
        loanProcessApprovalCommonService.log(approval);

        // activiti 弃单[结束流程]
        dealCancelTask(loanOrderDO.getProcessInstId());

        // 更新状态
        loanProcessDO.setOrderStatus(ORDER_STATUS_CANCEL);
        loanProcessDO.setCancelTaskDefKey(approval.getTaskDefinitionKey());

        loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);

        return ResultBean.ofSuccess(null, "弃单成功");
    }

    /**
     * 校验审核前提条件
     *
     * @param taskDefinitionKey
     * @param action
     * @param loanOrderDO
     * @param loanProcessDO
     */
    private void checkPreCondition(String taskDefinitionKey, Byte action, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {
        Preconditions.checkArgument(ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus()), "当前订单" + getOrderStatusText(loanProcessDO));

        // 【征信申请】时，若身份证有效期<=（today+7），不允许提交，提示“身份证已过期，不允许申请贷款”
        if (CREDIT_APPLY.getCode().equals(taskDefinitionKey) && ACTION_PASS.equals(action)) {
            // 众安征信接口校验（先关闭等ios过审核）
            List<LoanCustomerDO> loanCustomerDOS = loanCustomerDOMapper.selectCusByOrderId(loanOrderDO.getId());
            for (LoanCustomerDO loanCustomerDO : loanCustomerDOS) {
                ZhonganInfoDO zhonganInfoDO = zhonganInfoDOMapper.selectZNByOrderIdAndIdcard(loanOrderDO.getId(), loanCustomerDO.getIdCard());
                if (zhonganInfoDO == null) {
                    throw new BizException("客户:" + loanCustomerDO.getName() + "没有进行大数据查询,无法提交");
                } else {
                    /*if (!"成功".equals(zhonganInfoDO.getResultMessage())) {
                        throw new BizException("客户:" + zhonganInfoDO.getCustomerName() + zhonganInfoDO.getResultMessage() + ",无法提交征信");
                    }*/
                }
            }

            // 客户信息校验
            LoanCustomerDO loanCustomerDO = getLoanCustomer(loanOrderDO.getLoanCustomerId());
            String identityValidity = loanCustomerDO.getIdentityValidity();
            Preconditions.checkArgument(StringUtils.isNotBlank(identityValidity), "身份证有效期不能为空");

            // 2011.04.18-2031.04.18
            String[] split = identityValidity.split("\\-");
            String expireDateStr = split[1];

            // 长期
            if (CUST_ID_CARD_EXPIRE_DATE.equals(expireDateStr)) {
                return;
            }

            // 天数比较
            String[] expireDateStrArr = expireDateStr.split("\\.");
            Preconditions.checkArgument(expireDateStrArr.length == 3, "身份证有效期非法：" + identityValidity);
            Integer year = Integer.valueOf(expireDateStrArr[0]);
            Integer month = Integer.valueOf(expireDateStrArr[1]);
            Integer day = Integer.valueOf(expireDateStrArr[2]);
            Preconditions.checkArgument(year >= 1900 && year <= 2099 && month >= 1 && month <= 12 && day >= 1 && day <= 31,
                    "身份证有效期非法：" + identityValidity);
            LocalDate idCardExpireDate = LocalDate.of(year, month, day);

            LocalDate today = LocalDate.now();

            long daysDiff = DateTimeFormatUtils.daysDiff(today, idCardExpireDate);
            Preconditions.checkArgument(daysDiff > 7, "身份证已过期，不允许申请贷款");
        }

        // 【资料审核】
        else if (MATERIAL_REVIEW.getCode().equals(taskDefinitionKey) && ACTION_PASS.equals(action)) {
            // 提车资料必须已经提交了
            Preconditions.checkArgument(TASK_PROCESS_DONE.equals(loanProcessDO.getVehicleInformation()), "请先录入提车资料");
        }

        // 【业务申请】
        else if (LOAN_APPLY.getCode().equals(taskDefinitionKey)) {

            // PASS
            if (ACTION_PASS.equals(action)) {
                // 客户资料、车辆信息、金融方案  必须均已录入
                Preconditions.checkNotNull(loanOrderDO.getLoanCustomerId(), "请先录入客户信息");
                Preconditions.checkNotNull(loanOrderDO.getLoanCarInfoId(), "请先录入车辆信息");
                Preconditions.checkNotNull(loanOrderDO.getLoanFinancialPlanId(), "请先录入金融方案");

                // 紧急联系人不能少于2个
                List<LoanCustomerDO> loanCustomerDOS = loanCustomerDOMapper.listByPrincipalCustIdAndType(loanOrderDO.getLoanCustomerId(), CUST_TYPE_EMERGENCY_CONTACT, VALID_STATUS);
                Preconditions.checkArgument(null != loanCustomerDOS && loanCustomerDOS.size() >= 2, "紧急联系人不能少于2个");
            }

        }

        // 【业务付款申请】|| 【业务审批】|| 【放款审批】  -> PASS
        else if (
                (BUSINESS_PAY.getCode().equals(taskDefinitionKey)
                        || BUSINESS_REVIEW.getCode().equals(taskDefinitionKey)
                        || LOAN_REVIEW.getCode().equals(taskDefinitionKey))
                        && ACTION_PASS.equals(action)) {

            // 财务放款审批时，需要判断几个子业务的状态

            // 1、[金融方案修改申请]审批通过
            // 进行中的【金融方案修改申请】
            LoanFinancialPlanTempHisDO loanFinancialPlanTempHisDO = loanFinancialPlanTempHisDOMapper.lastByOrderId(loanOrderDO.getId());
            if (null != loanFinancialPlanTempHisDO) {
                Preconditions.checkArgument(APPLY_ORDER_PASS.equals(loanFinancialPlanTempHisDO.getStatus()), "当前订单已发起[金融方案修改申请]，请待审核通过后再操作！");
            }

            // 2、GPS安装完成
            LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
            Preconditions.checkNotNull(loanCarInfoDO, "车辆信息不存在");
            Integer gpsNum = loanCarInfoDO.getGpsNum();
            if (null != gpsNum && gpsNum > 0) {
                Preconditions.checkArgument(TASK_PROCESS_DONE.equals(loanProcessDO.getInstallGps()), "当前订单[GPS安装]未提交");
            }

//            // 3、（根据银行配置）视频面签完成   -仅：台州路桥支行
//            LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());
//            String bankName = loanBaseInfoDO.getBank();
//            if (BANK_NAME_ICBC_TaiZhou_LuQiao_Branch.equals(bankName)) {
//
//                Byte loanInfoRecordStatus = loanProcessDO.getLoanInfoRecord();
//                Preconditions.checkArgument(TASK_PROCESS_DONE.equals(loanInfoRecordStatus), "请先提交[视频面签登记]");
//            }

            // 4、前置校验
            boolean is_match_condition_bank = tel_verify_match_condition_bank(loanOrderDO.getLoanBaseInfoId());
            if (is_match_condition_bank) {

                // 若未开卡，提交业务付款申请单时候，提示：请先提交开卡申请
                String lastBankInterfaceSerialStatus = loanQueryDOMapper.selectLastBankInterfaceSerialStatusByTransCode(loanOrderDO.getLoanCustomerId(), IDict.K_TRANS_CODE.CREDITCARDAPPLY);
                Preconditions.checkArgument("1".equals(lastBankInterfaceSerialStatus) || "2".equals(lastBankInterfaceSerialStatus),
                        "请先提交[开卡申请]");
            }
        }

        // [资料流转（抵押资料 - 合伙人->公司]
        else if (isDataFlowMortgageP2cNewTask(taskDefinitionKey, action)) {
            Byte dataFlowMortgageP2cStatus = loanProcessDO.getDataFlowMortgageP2c();
            Preconditions.checkArgument(TASK_PROCESS_INIT.equals(dataFlowMortgageP2cStatus), "已新建过[抵押资料合伙人至公司]单据");
        }

        // [银行开卡]
        else if (BANK_OPEN_CARD.getCode().equals(taskDefinitionKey)) {
            // 前置开卡校验
            preCondition4BankOpenCard(loanOrderDO, loanProcessDO);
        }
    }

    /**
     * 订单状态Text
     *
     * @param loanProcessDO
     * @return
     */
    private String getOrderStatusText(LoanProcessDO loanProcessDO) {
        String orderStatusText = ORDER_STATUS_CANCEL.equals(loanProcessDO.getOrderStatus()) ? "[已弃单]" :
                (ORDER_STATUS_END.equals(loanProcessDO.getOrderStatus()) ? "[已结单]" : "[状态异常]");
        return orderStatusText;
    }

    /**
     * 征信增补
     *
     * @param approval
     * @param processInstanceId
     * @param loanProcessDO
     */
    private void execCreditSupplementTask(ApprovalParam approval, String processInstanceId, LoanProcessDO loanProcessDO) {

        // finish   ==》 open 【银行征信】/【社会征信】
        finishTask_(approval);

        approval.setOriginalTaskDefinitionKey(approval.getTaskDefinitionKey());

        // 判断当前任务流程   是否在电审前
        Preconditions.checkArgument(TASK_PROCESS_INIT.equals(loanProcessDO.getTelephoneVerify()), "流程已过[电审]，无法发起[征信增补]");

        // 当前所有task
        List<Task> currentTaskList = loanProcessApprovalCommonService.getCurrentTaskList(processInstanceId);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(currentTaskList), "无可执行任务");

        approval.setAction(ACTION_REJECT_AUTO);

        List<String> taskKeyList = Lists.newArrayList();

        currentTaskList.stream()
                .filter(Objects::nonNull)
                .forEach(task -> {

                    String taskDefinitionKey = task.getTaskDefinitionKey();

                    boolean isBankOrSocialCreditRecordTask = BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey)
                            || SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey);

                    boolean isLoanApplyOrVisitVerifyTask = (LOAN_APPLY.getCode().equals(taskDefinitionKey)
                            || VISIT_VERIFY.getCode().equals(taskDefinitionKey));

                    if (isBankOrSocialCreditRecordTask) {
                        approval.setTaskDefinitionKey(taskDefinitionKey);
                    }
                    // [贷款申请] && [上门家访]   -- 单个任务时，用当前KEY即可
                    else if (isLoanApplyOrVisitVerifyTask) {
                        approval.setTaskDefinitionKey(taskDefinitionKey);
                    }

                    taskKeyList.add(taskDefinitionKey);
                });


        boolean isLoanApplyAndVisitVerifyTask = taskKeyList.contains(LOAN_APPLY.getCode())
                && taskKeyList.contains(VISIT_VERIFY.getCode());

        // [贷款申请] && [上门家访]   -- 2个任务时
        if (isLoanApplyAndVisitVerifyTask) {

            // 必须用[贷款申请]-KEY
            approval.setTaskDefinitionKey(LOAN_APPLY.getCode());
        }
    }

    /**
     * 执行【005-抵押资料合伙人至公司】任务
     *
     * @param approval
     */
    private void execDataFlowMortgageP2cNewFilterTask(ApprovalParam approval) {
        // 通过 前置隐藏拦截任务     PASS -> Filter-Task
        approval.setTaskDefinitionKey(DATA_FLOW_MORTGAGE_P2C_NEW_FILTER.getCode());
        approval.setAction(ACTION_PASS);
        approval.setNeedPush(false);
    }

    /**
     * 是否【征信增补】
     *
     * @param taskDefinitionKey
     * @param action
     * @return
     */
    private boolean isCreditSupplementTask(String taskDefinitionKey, Byte action) {
        boolean isCreditSupplementTask = CREDIT_SUPPLEMENT.getCode().equals(taskDefinitionKey) && ACTION_PASS.equals(action);
        return isCreditSupplementTask;
    }

    /**
     * 是否为：【发起/提交】资料增补单
     *
     * @param approval
     * @return
     */
    private boolean isInfoSupplementTask(ApprovalParam approval) {
        // 发起资料增补
        boolean isStartInfoSupplementTask = ACTION_INFO_SUPPLEMENT.equals(approval.getAction());
        // 处理资料增补单（暂时-只有PASS操作 -即：END）
        boolean isEndInfoSupplementTask = INFO_SUPPLEMENT.getCode().equals(approval.getTaskDefinitionKey());
        boolean isInfoSupplementTask = isStartInfoSupplementTask || isEndInfoSupplementTask;
        return isInfoSupplementTask;
    }

    /**
     * 执行资料增补任务
     *
     * @param approval
     * @param loanProcessDO
     * @return
     */
    private ResultBean<Void> execInfoSupplementTask(ApprovalParam approval, LoanProcessDO loanProcessDO) {

        // 【发起】资料增补单
        if (ACTION_INFO_SUPPLEMENT.equals(approval.getAction())) {

            // 创建增补单
            startInfoSupplement(approval);

            return ResultBean.ofSuccess(null, "[资料增补]发起成功");
        }

        // 【提交】资料增补单
        else if (INFO_SUPPLEMENT.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {

            // 提交增补单
            endInfoSupplement(approval);

            // 异步打包文件
            asyncPackZipFile(approval.getTaskDefinitionKey(), approval.getAction(), loanProcessDO, 2);

            return ResultBean.ofSuccess(null, "[资料增补]提交成功");
        }

        return ResultBean.ofError("action参数有误");
    }

    /**
     * 执行任务
     *
     * @param task
     * @param variables
     * @param approval
     * @param loanOrderDO
     * @param loanProcessDO
     */
    private void execTask(Task task, Map<String, Object> variables, ApprovalParam approval, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {
        // 电审任务
        if (TELEPHONE_VERIFY.getCode().equals(approval.getTaskDefinitionKey())) {
            // 执行电审任务
            execTelephoneVerifyTask(task, variables, approval, loanOrderDO, loanProcessDO);
        } else {
            // 其他任务：直接提交
            loanProcessApprovalCommonService.completeTask(task.getId(), variables);
        }

        // 征信申请记录拦截
        execCreditRecordFilterTask(task, loanOrderDO.getProcessInstId(), approval, variables);

        // 业务申请 & 上门调查 拦截
        execLoanApplyVisitVerifyFilterTask(task, loanOrderDO.getProcessInstId(), approval, variables, loanOrderDO, loanProcessDO);
    }

    /**
     * 更新新产生的任务状态
     *
     * @param loanProcessDO
     * @param processInstanceId
     * @param startTaskList
     * @param approval
     * @param currentLoanProcessDO
     */
    private void updateNextTaskProcessStatus(LoanProcessDO loanProcessDO, String processInstanceId, List<Task> startTaskList,
                                             ApprovalParam approval, LoanProcessDO currentLoanProcessDO) {

        List<String> startTaskIdList = getTaskIdList(startTaskList);

        // 获取提交之后的待执行任务列表
        List<Task> endTaskList = loanProcessApprovalCommonService.getCurrentTaskList(processInstanceId);

        List<String> endTaskIdList = Lists.newArrayList();

        if (CollectionUtils.isEmpty(endTaskList)) {
            return;
        }

        // 筛选出新产生、旧有、以及附带被完成 的任务
        List<Task> newTaskList = Lists.newArrayList();
        List<Task> oldTaskList = Lists.newArrayList();
        List<Task> doneTaskList = Lists.newArrayList();

        endTaskList.stream()
                .filter(Objects::nonNull)
                .forEach(task -> {

                    if (startTaskIdList.contains(task.getId())) {
                        // 存在：旧有的任务
                        oldTaskList.add(task);

                    } else {

                        // 不存在：新产生的任务
                        newTaskList.add(task);
                    }

                    endTaskIdList.add(task.getId());
                });

        startTaskList.stream()
                .forEach(task -> {

                    if (!endTaskIdList.contains(task.getId())) {

                        doneTaskList.add(task);
                    }
                });


        Byte action = approval.getAction();
        if (ACTION_PASS.equals(action)) {

            // new  -> TO_DO     old -> 不变
            doUpdateNextTaskProcessStatus(newTaskList, loanProcessDO, TASK_PROCESS_TODO, approval);

        } else if (ACTION_REJECT_MANUAL.equals(action) || ACTION_REJECT_AUTO.equals(action)) {

            // new  ->  REJECT
            doUpdateNextTaskProcessStatus(newTaskList, loanProcessDO, TASK_PROCESS_REJECT, approval);

            // done -> INIT
            doUpdateNextTaskProcessStatus(doneTaskList, loanProcessDO, TASK_PROCESS_INIT, approval);

            // old  ->  INIT
            // 没过电审
            if (!TASK_PROCESS_DONE.equals(currentLoanProcessDO.getTelephoneVerify())) {

                // 移除不需要打回的节点
                removeNotNeedRejectTasks(oldTaskList, approval);

                // old  ->  INIT
                doUpdateNextTaskProcessStatus(oldTaskList, loanProcessDO, TASK_PROCESS_INIT, approval);

            } else {
                // 过了电审，则不是真正的全部打回      nothing
            }

            // 打回记录
            loanProcessApprovalCommonService.createRejectLog(newTaskList, approval.getOrderId(),
                    approval.getTaskDefinitionKey(), approval.getInfo());

        } else if (ACTION_CANCEL.equals(action)) {
            // nothing
        }
    }

    private List<String> getTaskIdList(List<Task> startTaskList) {

        if (!CollectionUtils.isEmpty(startTaskList)) {

            List<String> startTaskIdList = startTaskList.stream()
                    .filter(Objects::nonNull)
                    .map(Task::getId)
                    .collect(toList());

            return startTaskIdList;
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * 移除不需要打回的节点
     *
     * @param oldTaskList
     * @param approval
     */
    private void removeNotNeedRejectTasks(List<Task> oldTaskList, ApprovalParam approval) {

        // [电审] 的并行节点 -> [银行开卡] 不能随[电审]一起打回
        if (TELEPHONE_VERIFY.getCode().equals(approval.getTaskDefinitionKey())) {

            if (!CollectionUtils.isEmpty(oldTaskList)) {

                List<Task> removeTaskList = oldTaskList.stream()
                        .filter(Objects::nonNull)
                        .filter(task -> BANK_OPEN_CARD.getCode().equals(task.getTaskDefinitionKey()))
                        .collect(toList());


                oldTaskList.removeAll(removeTaskList);
            }

        }
    }


    private String getRejectToTask(String taskDefinitionKey) {
        if (BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode().equals(taskDefinitionKey)) {
            return CREDIT_APPLY.getCode();
        }
        return taskDefinitionKey;
    }

    /**
     * 更新待执行的任务状态
     *
     * @param nextTaskList
     * @param loanProcessDO
     * @param taskProcessStatus 未提交/打回
     * @param approval
     */
    private void doUpdateNextTaskProcessStatus(List<Task> nextTaskList, LoanProcessDO loanProcessDO, Byte taskProcessStatus, ApprovalParam approval) {

        if (!CollectionUtils.isEmpty(nextTaskList)) {
            nextTaskList.stream()
                    .filter(Objects::nonNull)
                    .forEach(task -> {
                        // 未提交
                        loanProcessApprovalCommonService.updateCurrentTaskProcessStatus(loanProcessDO, task.getTaskDefinitionKey(), taskProcessStatus, approval);
                    });
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
        Preconditions.checkArgument(count > 0, "[资料增补]发起失败");
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

        // 增补提交时间
        loanInfoSupplementDO.setEndTime(new Date());

        // 审核备注
        loanInfoSupplementDO.setRemark(approval.getInfo());

        // 已处理状态
        loanInfoSupplementDO.setStatus(TASK_PROCESS_DONE);

        int count = loanInfoSupplementDOMapper.updateByPrimaryKeySelective(loanInfoSupplementDO);
        Preconditions.checkArgument(count > 0, "[资料增补]提交失败");
    }

    /**
     * 获取 LoanBaseInfoDO
     *
     * @param loanBaseInfoId
     * @return
     */
    private LoanBaseInfoDO getLoanBaseInfoDO(Long loanBaseInfoId) {
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);
        Preconditions.checkNotNull(loanBaseInfoDO, "数据异常，贷款基本信息为空");
        Preconditions.checkNotNull(loanBaseInfoDO.getLoanAmount(), "数据异常，贷款金额为空");
        Preconditions.checkNotNull(loanBaseInfoDO.getBank(), "数据异常，贷款银行为空");

        return loanBaseInfoDO;
    }

    /**
     * 获取 LoanCustomerDO
     *
     * @param loanCustomerId
     * @return
     */
    private LoanCustomerDO getLoanCustomer(Long loanCustomerId) {
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanCustomerId, null);
        Preconditions.checkNotNull(loanCustomerDO, "数据异常，主贷人信息为空");

        return loanCustomerDO;
    }

    /**
     * 获取 LoanRefundApplyDO
     *
     * @param orderId
     * @return
     */
    private LoanRefundApplyDO getLoanRefundApply(Long orderId) {
        LoanRefundApplyDO loanRefundApplyDO = loanRefundApplyDOMapper.lastByOrderId(orderId);
        Preconditions.checkNotNull(loanRefundApplyDO, "[退款申请单]不存在");

        return loanRefundApplyDO;
    }

    /**
     * 执行电审任务
     *
     * @param task
     * @param variables
     * @param approval
     * @param loanOrderDO
     * @param loanProcessDO
     */
    private void execTelephoneVerifyTask(Task task, Map<String, Object> variables, ApprovalParam approval,
                                         LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        // 角色
        Set<String> userGroupNameSet = permissionService.getLoginUserHasUserGroups();
        // 最大电审角色等级
        Byte maxRoleLevel = getTelephoneVerifyMaxRole(userGroupNameSet);
        // 电审专员及以上有权电审
        Preconditions.checkArgument(null != maxRoleLevel && maxRoleLevel >= LEVEL_TELEPHONE_VERIFY_COMMISSIONER, "您无[电审]权限");

        // 如果是审核通过
        if (ACTION_PASS.equals(approval.getAction())) {

            // 获取贷款额度
            LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
            Preconditions.checkArgument(null != loanFinancialPlanDO && null != loanFinancialPlanDO.getLoanAmount(), "贷款额不能为空");
            double loanAmount = loanFinancialPlanDO.getLoanAmount().doubleValue();

            // 直接通过
            if (loanAmount >= 0 && loanAmount <= 100000) {
                // 完成任务：全部角色直接过单
                passTelephoneVerifyTask(task, variables, approval, loanOrderDO, loanProcessDO);
            } else if (loanAmount > 100000 && loanAmount <= 300000) {
                // 电审主管以上可过单
                if (maxRoleLevel < LEVEL_TELEPHONE_VERIFY_LEADER) {
                    // 记录
                    updateTelephoneVerify(loanOrderDO.getId(), maxRoleLevel);
                } else {
                    // 完成任务
                    passTelephoneVerifyTask(task, variables, approval, loanOrderDO, loanProcessDO);
                }
            } else if (loanAmount > 300000 && loanAmount <= 500000) {
                // 电审经理以上可过单
                if (maxRoleLevel < LEVEL_TELEPHONE_VERIFY_MANAGER) {
                    // 记录
                    updateTelephoneVerify(loanOrderDO.getId(), maxRoleLevel);
                } else {
                    // 完成任务
                    passTelephoneVerifyTask(task, variables, approval, loanOrderDO, loanProcessDO);
                }
            } else if (loanAmount > 500000) {
                // 总监以上可过单
                if (maxRoleLevel < LEVEL_DIRECTOR) {
                    // 记录
                    updateTelephoneVerify(loanOrderDO.getId(), maxRoleLevel);
                } else {
                    // 完成任务
                    passTelephoneVerifyTask(task, variables, approval, loanOrderDO, loanProcessDO);
                }
            }
        } else if (ACTION_CANCEL.equals(approval.getAction())) {
            // 弃单，直接提交
            loanProcessApprovalCommonService.completeTask(task.getId(), variables);
        } else if (ACTION_REJECT_MANUAL.equals(approval.getAction())) {
            // 手动打回
            variables.put(PROCESS_VARIABLE_TARGET, LOAN_APPLY.getCode());
            loanProcessApprovalCommonService.completeTask(task.getId(), variables);
        } else if (ACTION_REJECT_AUTO.equals(approval.getAction())) {
            // 自动打回
            variables.put(PROCESS_VARIABLE_TARGET, CREDIT_APPLY.getCode());
            loanProcessApprovalCommonService.completeTask(task.getId(), variables);
        }
    }

    /**
     * 【电审】过单
     *
     * @param task
     * @param variables
     * @param approval
     * @param loanOrderDO
     * @param loanProcessDO
     */
    private void passTelephoneVerifyTask(Task task, Map<String, Object> variables, ApprovalParam approval,
                                         LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        // 完成任务：全部角色直接过单
        loanProcessApprovalCommonService.completeTask(task.getId(), variables);
        // 自动执行【金融方案】任务
        autoCompleteTask(task.getProcessInstanceId(), approval.getOrderId(), FINANCIAL_SCHEME.getCode());
        // 自动执行【待收钥匙】任务
        completeCommitKeyTask(task.getProcessInstanceId(), approval.getOrderId());
        // 更新状态
        updateTelephoneVerify(approval.getOrderId(), TASK_PROCESS_DONE);
    }

    /**
     * 前置开卡校验
     *
     * @param loanOrderDO
     * @param loanProcessDO
     */
    private void preCondition4BankOpenCard(LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        // 是否 走[银行开卡]
        boolean is_match_condition_bank = tel_verify_match_condition_bank(loanOrderDO.getLoanBaseInfoId());

        // 走[银行开卡]
        if (is_match_condition_bank) {

            LoanCustomerDO loanCustomerDO = getLoanCustomer(loanOrderDO.getLoanCustomerId());
            String openCardOrder = loanCustomerDO.getOpenCardOrder();

            // 是否前置开卡     -是：[银行开卡]-必须PASS
            if (StringUtils.isNotBlank(openCardOrder) && K_YORN_NO.equals(Byte.valueOf(openCardOrder))) {

                Preconditions.checkArgument(TASK_PROCESS_DONE.equals(loanProcessDO.getTelephoneVerify()),
                        "前先提交[" + TELEPHONE_VERIFY.getName() + "]");
            }
        }
    }

    /**
     * 自动执行【待收钥匙】任务
     *
     * @param processInstanceId
     * @param orderId
     */
    private void completeCommitKeyTask(String processInstanceId, Long orderId) {

        Byte carKey = loanCarInfoDOMapper.getCarKeyByOrderId(orderId);
        // 不留备用钥匙
        if (CAR_KEY_FALSE.equals(carKey)) {
            autoCompleteTask(processInstanceId, orderId, COMMIT_KEY.getCode());
        }
    }

    /**
     * 自动完成指定任务  -自动PASS
     *
     * @param processInstanceId
     * @param orderId
     * @param taskDefinitionKey
     */
    private void autoCompleteTask(String processInstanceId, Long orderId, String taskDefinitionKey) {

        Map<String, Object> variables = Maps.newHashMap();
        variables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey)
                .singleResult();

        Preconditions.checkNotNull(task, "[" + LoanProcessEnum.getNameByCode(taskDefinitionKey) + "]任务不存在");

        ApprovalParam approval = new ApprovalParam();
        approval.setOrderId(orderId);
        approval.setAction(ACTION_PASS);
        approval.setTaskDefinitionKey(taskDefinitionKey);

        loanProcessApprovalCommonService.completeTask(task.getId(), variables);

        // 更新流程记录
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(orderId);
        loanProcessApprovalCommonService.updateCurrentTaskProcessStatus(loanProcessDO, taskDefinitionKey, TASK_PROCESS_DONE, approval);
        loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);
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
        // 本地表更新
        loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);
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
                .filter(StringUtils::isNotBlank)
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
     * 业务申请 & 上门调查 拦截
     *
     * @param currentTask
     * @param processInstId
     * @param approval
     * @param variables
     * @param loanOrderDO
     * @param loanProcessDO
     */
    private void execLoanApplyVisitVerifyFilterTask(Task currentTask, String processInstId, ApprovalParam approval,
                                                    Map<String, Object> variables, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        // 正常-过来的拦截任务
        boolean loanApplyVisitVerifyFilterTask = isLoanApplyVisitVerifyFilterTask(approval.getTaskDefinitionKey(), variables);

        // [社会征信]-过来的拦截任务        ==>   [社会征信] & [PASS] & [ target -> 贷款申请-filter     贷款申请 补充 社会征信 ]
        Object target = variables.get(PROCESS_VARIABLE_TARGET);
        boolean is_socialTask__target_is__LoanApplyVisitVerifyFilterTask =
                SOCIAL_CREDIT_RECORD.getCode().equals(approval.getTaskDefinitionKey())
                        && ACTION_PASS.equals(approval.getAction())
                        && LOAN_APPLY_VISIT_VERIFY_FILTER.getCode().equals(target);

        // 执行拦截任务
        if (loanApplyVisitVerifyFilterTask || is_socialTask__target_is__LoanApplyVisitVerifyFilterTask) {

            // 获取所有正在执行的并行任务
            List<Task> tasks = loanProcessApprovalCommonService.getCurrentTaskList(processInstId);

            // 上门调查：只有【提交】;  业务申请：只有【提交】&【弃单】;      -均无[打回]
            // PASS
            if (ACTION_PASS.equals(approval.getAction())) {
                doLoanApplyVisitVerifyFilterTask_Pass(currentTask, tasks, loanOrderDO, loanProcessDO);
            }
            // CANCEL
            else if (ACTION_CANCEL.equals(approval.getAction())) {
                dealCancelTask(processInstId);
            }
            // AUTO_REJECT
            else if (ACTION_REJECT_AUTO.equals(approval.getAction())) {
                doLoanApplyVisitVerifyTask_AutoReject(currentTask, tasks, approval);
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
    private void doLoanApplyVisitVerifyTask_AutoReject(Task currentTask, List<Task> tasks, ApprovalParam approvalParam) {
        // 打回
        dealLoanApplyVisitVerifyAutoRejectTask(currentTask, tasks);

        // update process
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(approvalParam.getOrderId());
        loanProcessDO.setLoanApply(TASK_PROCESS_INIT);
        loanProcessDO.setVisitVerify(TASK_PROCESS_INIT);
        loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);

        // 自动提交打回的【征信申请】
        approvalParam.setTaskDefinitionKey(CREDIT_APPLY.getCode());
        approvalParam.setAction(ACTION_PASS);
        approvalParam.setNeedLog(false);
        approvalParam.setCheckPermission(false);
        approval(approvalParam);
    }

    /**
     * 并行任务：-通过
     *
     * @param currentTask
     * @param tasks
     * @param loanOrderDO
     * @param loanProcessDO
     */
    private void doLoanApplyVisitVerifyFilterTask_Pass(Task currentTask, List<Task> tasks, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        // 是否都通过了      -> 既非LOAN_APPLY，也非VISIT_VERIFY
        if (!CollectionUtils.isEmpty(tasks)) {
            long count = tasks.stream()
                    .filter(Objects::nonNull)
                    .filter(e -> LOAN_APPLY.getCode().equals(e.getTaskDefinitionKey())
                            || VISIT_VERIFY.getCode().equals(e.getTaskDefinitionKey())
                            || SOCIAL_CREDIT_RECORD.getCode().equals(e.getTaskDefinitionKey()))
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

                                // 是否会走 [银行开卡]
                                yes_or_not_go_bankOpenCardTask(passVariables, loanOrderDO, loanProcessDO);

                                // "主任务"  ->  执行通过
                                loanProcessApprovalCommonService.completeTask(task.getId(), passVariables);
                            }

                            // filter-task
                            else if (LOAN_APPLY_VISIT_VERIFY_FILTER.getCode().equals(task.getTaskDefinitionKey())
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
     * 是否会走 [银行开卡]
     *
     * @param passVariables
     * @param loanOrderDO
     * @param loanProcessDO
     */
    private void yes_or_not_go_bankOpenCardTask(Map<String, Object> passVariables, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        // 是否 走[银行开卡]
        boolean is_match_condition_bank = tel_verify_match_condition_bank(loanOrderDO.getLoanBaseInfoId());

        // 走[银行开卡]
        if (is_match_condition_bank) {

            // 已经走过一次 [银行开卡]
            if (!TASK_PROCESS_INIT.equals(loanProcessDO.getBankOpenCard())) {

                // 直接走[电审]
                passVariables.put(PROCESS_VARIABLE_TARGET, TELEPHONE_VERIFY.getCode());
            }

            // 第一次走 [银行开卡]
            else {

                // 走 [电审] + [银行开卡]
                passVariables.put(PROCESS_VARIABLE_TARGET, StringUtils.EMPTY);
            }

        }

        // 不走[银行开卡]
        else {

            // 直接走[电审]
            passVariables.put(PROCESS_VARIABLE_TARGET, TELEPHONE_VERIFY.getCode());
        }
    }

    /**
     * 是否 走[银行开卡]
     *
     * @param loanBaseInfoId
     * @return
     */
    private boolean tel_verify_match_condition_bank(Long loanBaseInfoId) {
        // 贷款银行
        LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanBaseInfoId);
        String bankName = loanBaseInfoDO.getBank();

        // 城站支行 || 台州支行
        boolean is_match_condition_bank = BANK_NAME_ICBC_HangZhou_City_Station_Branch.equals(bankName)
                || BANK_NAME_ICBC_TaiZhou_LuQiao_Branch.equals(bankName);

        return is_match_condition_bank;
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
            List<Task> tasks = loanProcessApprovalCommonService.getCurrentTaskList(processInstId);

            // PASS
            if (ACTION_PASS.equals(approval.getAction())) {
                dealCreditRecordTask_pass(currentTask, tasks);
            }
            // REJECT
            else if (ACTION_REJECT_MANUAL.equals(approval.getAction())) {
                dealCreditRecordTask_reject(currentTask, tasks);
            }
            // CANCEL
            else if (ACTION_CANCEL.equals(approval.getAction())) {
                dealCancelTask(currentTask.getProcessInstanceId());
            }
            // AUTO_REJECT
            else if (ACTION_REJECT_AUTO.equals(approval.getAction())) {
                dealCreditRecordTask_autoReject(currentTask, tasks, approval);
            }
        }

        // 小于13W:  单银行征信录入 & 自动打回操作时
        else if (isOnlyOneBankCreditRecordTask(variables, approval.getTaskDefinitionKey())) {
            // AUTO_REJECT
            if (ACTION_REJECT_AUTO.equals(approval.getAction())) {

                // 提交【征信申请】
                dealCreditApplyTask_pass(approval);
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

    private void dealCreditRecordTask_autoReject(Task currentTask, List<Task> tasks, ApprovalParam approvalParam) {
        // 打回到原点【征信申请】
        dealCreditRecordTask_reject(currentTask, tasks);

        // 提交【征信申请】
        dealCreditApplyTask_pass(approvalParam);
    }

    /**
     * 提交【征信申请】
     *
     * @param approval
     */
    private void dealCreditApplyTask_pass(ApprovalParam approval) {
        // update process
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(approval.getOrderId());
        loanProcessDO.setBankCreditRecord(TASK_PROCESS_INIT);
        loanProcessDO.setSocialCreditRecord(TASK_PROCESS_INIT);
        loanProcessApprovalCommonService.updateLoanProcess(loanProcessDO);

        // 自动提交打回的【征信申请】
        approval.setTaskDefinitionKey(CREDIT_APPLY.getCode());
        approval.setAction(ACTION_PASS);
        approval.setNeedLog(false);
        approval.setCheckPermission(false);
        approval(approval);
    }

    @Override
    public ResultBean<List<TaskStateVO>> currentTask(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        List<Task> loanProcessTaskList = taskService.createTaskQuery()
                .processInstanceId(loanOrderDO.getProcessInstId())
                .list();

        List<TaskEntityImpl> insteadPayTaskList = activitiDeploymentMapper.listInsteadPayTaskByOrderId(orderId);
        List<TaskEntityImpl> collectionTaskList = activitiDeploymentMapper.listCollectionTaskByOrderId(orderId);
        List<TaskEntityImpl> legalTaskList = activitiDeploymentMapper.listLegalTaskByOrderId(orderId);
        List<TaskEntityImpl> bridgeTaskList = activitiDeploymentMapper.listBridgeTaskByOrderId(orderId);

        List<Task> runTaskList = Lists.newArrayList();
        runTaskList.addAll(insteadPayTaskList);
        runTaskList.addAll(collectionTaskList);
        runTaskList.addAll(loanProcessTaskList);
        runTaskList.addAll(legalTaskList);
        runTaskList.addAll(bridgeTaskList);

        List<TaskStateVO> taskStateVOS = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(runTaskList)) {
            taskStateVOS = runTaskList.stream()
                    .filter(Objects::nonNull)
                    .filter(task -> !task.getTaskDefinitionKey().startsWith("filter"))
                    .map(task -> {

                        TaskStateVO taskStateVO = new TaskStateVO();
                        taskStateVO.setTaskDefinitionKey(task.getTaskDefinitionKey());
                        taskStateVO.setTaskId(task.getId());
                        taskStateVO.setTaskName(task.getName());
                        taskStateVO.setTaskStatus(TASK_PROCESS_TODO);
                        taskStateVO.setTaskStatusText(getTaskStatusText(TASK_PROCESS_TODO));

                        return taskStateVO;
                    })
                    .collect(toList());
        }

        return ResultBean.ofSuccess(taskStateVOS, "查询当前流程任务节点信息成功");
    }

    @Override
    public ResultBean<TaskStateVO> taskStatus(Long orderId, String taskDefinitionKey, Long processId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(taskDefinitionKey), "任务Key不能为空");

        LoanProcessDO_ loanProcessDO_ = loanProcessApprovalCommonService.getLoanProcess_(orderId, processId, taskDefinitionKey);

        LoanProcessDO loanProcessDO = null;
        if (loanProcessDO_ instanceof LoanProcessDO) {
            loanProcessDO = (LoanProcessDO) loanProcessDO_;
        } else {
            loanProcessDO = loanProcessApprovalCommonService.getLoanProcess(orderId);
        }

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

            if (FINANCIAL_SCHEME_MODIFY_APPLY.getCode().equals(taskDefinitionKey)) {

                // 历史进行中的申请单
                LoanFinancialPlanTempHisDO loanFinancialPlanTempHisDO = loanFinancialPlanTempHisDOMapper.lastByOrderId(orderId);
                if (null != loanFinancialPlanTempHisDO) {

                    switch (loanFinancialPlanTempHisDO.getStatus()) {
                        case 0:
                            taskStatus = 2;
                            break;
                        case 1:
                            taskStatus = 1;
                            break;
                        case 2:
                            taskStatus = 1;
                            break;
                        case 3:
                            taskStatus = 3;
                            break;
                        case 12:
                            taskStatus = 12;
                            break;
                    }
                }

            } else if (FINANCIAL_SCHEME_MODIFY_APPLY_REVIEW.getCode().equals(taskDefinitionKey)) {

                // 历史进行中的申请单
                LoanFinancialPlanTempHisDO loanFinancialPlanTempHisDO = loanFinancialPlanTempHisDOMapper.lastByOrderId(orderId);
                if (null != loanFinancialPlanTempHisDO) {

                    switch (loanFinancialPlanTempHisDO.getStatus()) {
                        case 1:
                            taskStatus = 1;
                            break;
                        case 2:
                            taskStatus = 2;
                            break;
                        case 3:
                            taskStatus = 3;
                            break;
                        case 4:
                            taskStatus = 2;
                            break;
                        case 5:
                            taskStatus = 2;
                            break;
                        case 6:
                            taskStatus = 2;
                            break;
                        case 12:
                            taskStatus = 12;
                            break;
                    }
                }

            } else if (REFUND_APPLY.getCode().equals(taskDefinitionKey)) {

                // 历史进行中的申请单
                LoanRefundApplyDO loanRefundApplyDO = loanRefundApplyDOMapper.lastByOrderId(orderId);
                if (null != loanRefundApplyDO) {

                    switch (loanRefundApplyDO.getStatus()) {
                        case 0:
                            taskStatus = 2;
                            break;
                        case 1:
                            taskStatus = 1;
                            break;
                        case 2:
                            taskStatus = 1;
                            break;
                        case 3:
                            taskStatus = 3;
                            break;
                        case 12:
                            taskStatus = 12;
                            break;
                    }
                }

            } else if (REFUND_APPLY_REVIEW.getCode().equals(taskDefinitionKey)) {

                // 历史进行中的申请单
                LoanRefundApplyDO loanRefundApplyDO = loanRefundApplyDOMapper.lastByOrderId(orderId);
                if (null != loanRefundApplyDO) {

                    switch (loanRefundApplyDO.getStatus()) {
                        case 1:
                            taskStatus = 1;
                            break;
                        case 2:
                            taskStatus = 2;
                            break;
                        case 3:
                            taskStatus = 3;
                            break;
                        case 12:
                            taskStatus = 12;
                            break;
                    }
                }

            } else if (OUTWORKER_COST_APPLY.getCode().equals(taskDefinitionKey)) {

                // 外勤费用申报
                LegworkReimbursementDO legworkReimbursementDO = legworkReimbursementDOMapper.selectByPrimaryKey(orderId);
                if (null != legworkReimbursementDO) {

                    switch (legworkReimbursementDO.getStatus()) {
                        case 0:
                            taskStatus = 0;
                            break;
                        case 1:
                            taskStatus = 1;
                            break;
                        case 2:
                            taskStatus = 2;
                            break;
                        case 3:
                            taskStatus = 3;
                            break;
                        case 4:
                            taskStatus = 1;
                            break;
                    }
                }

            } else if (OUTWORKER_COST_APPLY_REVIEW.getCode().equals(taskDefinitionKey)) {

                // 财务报销
                LegworkReimbursementDO legworkReimbursementDO = legworkReimbursementDOMapper.selectByPrimaryKey(orderId);
                if (null != legworkReimbursementDO) {

                    switch (legworkReimbursementDO.getStatus()) {
                        case 0:
                            taskStatus = 0;
                            break;
                        case 1:
                            taskStatus = 1;
                            break;
                        case 2:
                            taskStatus = 0;
                            break;
                        case 3:
                            taskStatus = 0;
                            break;
                        case 4:
                            taskStatus = 2;
                            break;
                    }
                }

            } else {
                taskStatus = getTaskStatus(loanProcessDO_, taskDefinitionKey);
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
        if (null == taskStatus) {
            return "-";
        }
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
            case 4:
                taskStatusText = "未提交";
                break;
            case 5:
                taskStatusText = "未提交";
                break;
            case 6:
                taskStatusText = "未提交";
                break;
            case 7:
                taskStatusText = "未提交";
                break;
            case 12:
                taskStatusText = "已弃单";
                break;
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

                        // Spike  于  2017-12-29 13:07:00  通过  xx业务
                        String history = e.getUserName()
                                + " 于 "
                                + convertApprovalDate(e.getCreateTime())
                                + " "
                                + convertActionText(e.getAction())
                                + " "
                                + convertTaskDefKeyText(e.getTaskDefinitionKey())
                                + getInfo(e.getAction(), e.getInfo());

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

    @Override
    public ResultBean<LoanRejectLogVO> rejectLog(Long orderId, String taskDefinitionKey) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(taskDefinitionKey), "任务节点不能为空");

        LoanRejectLogVO loanRejectLogVO = new LoanRejectLogVO();

        LoanRejectLogDO loanRejectLogDO = loanRejectLogService.rejectLog(orderId, taskDefinitionKey);
        if (null != loanRejectLogDO) {
            BeanUtils.copyProperties(loanRejectLogDO, loanRejectLogVO);
            loanRejectLogVO.setOrderId(String.valueOf(loanRejectLogDO.getOrderId()));
        }

        return ResultBean.ofSuccess(loanRejectLogVO);
    }

    /**
     * 打回/弃单 理由
     *
     * @param action
     * @param info
     * @return
     */
    private String getInfo(Byte action, String info) {

        if (ACTION_REJECT_MANUAL.equals(action)
                || ACTION_REJECT_AUTO.equals(action)
                || ACTION_CANCEL.equals(action)) {

            return "    理由：" + (StringUtils.isBlank(info) ? "" : info);
        }

        return "";
    }

    /**
     * action字面意义转换
     *
     * @param action 0-打回; 1-通过(提交); 2-弃单; 3-资料增补;
     * @return
     */
    public static String convertActionText(Byte action) {
        String actionText = null;

        switch (action) {
            case -1:
                actionText = "自动打回";
                break;
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
            case 4:
                actionText = "新建";
                break;
            case 5:
                actionText = "反审";
                break;
            case 6:
                actionText = "领取";
                break;
            case 7:
                actionText = "取消领取";
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
        String taskName = LoanProcessEnum.getNameByCode(taskDefinitionKey);
        return StringUtils.isBlank(taskName) ? "" : taskName;
    }

    /**
     * 执行 征信申请审核 或 银行&社会征信录入 任务时：填充流程变量-贷款金额
     *
     * @param variables
     * @param action
     * @param taskDefinitionKey
     * @param loanOrderDO
     */
    private void fillLoanAmount(Map<String, Object> variables, Byte action, String taskDefinitionKey, LoanOrderDO loanOrderDO) {

        // [征信申请] & [PASS]
        boolean isCreditApplyTaskAndActionIsPass = CREDIT_APPLY.getCode().equals(taskDefinitionKey) && ACTION_PASS.equals(action);

        // [银行&社会征信录入]
        boolean isBankAndSocialCreditRecordTask = BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey) || SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey);

        if (isCreditApplyTaskAndActionIsPass || isBankAndSocialCreditRecordTask) {

            // 预计贷款金额
            LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());

            // 流程变量
            variables.put(PROCESS_VARIABLE_LOAN_AMOUNT_EXPECT, loanBaseInfoDO.getLoanAmount());
        }
    }


    /**
     * 设置并返回流程变量
     *
     * @param currentExecTask
     * @param approval
     * @param loanOrderDO
     * @param loanProcessDO
     */
    private Map<String, Object> setAndGetVariables(Task currentExecTask, ApprovalParam approval, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {
        Map<String, Object> variables = Maps.newHashMap();

        // 流程变量：action
        variables.put(PROCESS_VARIABLE_ACTION, approval.getAction());

        // 添加流程变量-贷款金额
        fillLoanAmount(variables, approval.getAction(), currentExecTask.getTaskDefinitionKey(), loanOrderDO);

        // 填充其他的流程变量
        fillOtherVariables(variables, approval, loanProcessDO, loanOrderDO);

        return variables;
    }

    /**
     * 填充其他的流程变量
     *
     * @param variables
     * @param approval
     * @param loanProcessDO
     * @param loanOrderDO
     */
    private void fillOtherVariables(Map<String, Object> variables, ApprovalParam approval, LoanProcessDO loanProcessDO, LoanOrderDO loanOrderDO) {

        String taskDefinitionKey = approval.getTaskDefinitionKey();
        Byte action = approval.getAction();

        // [贷款申请]
        if (LOAN_APPLY.getCode().equals(taskDefinitionKey)) {

            // 1
            if (ACTION_PASS.equals(action)) {

                // 预计贷款金额
                LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());
                Byte expectLoanAmount = loanBaseInfoDO.getLoanAmount();

                // 实际贷款额度
                LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
                Double actualLoanAmount = null;

                if (ACTION_PASS.equals(action)) {
                    Preconditions.checkArgument(null != loanFinancialPlanDO && null != loanFinancialPlanDO.getLoanAmount(), "贷款额不能为空");
                    actualLoanAmount = loanFinancialPlanDO.getLoanAmount().doubleValue();
                }

                // 预计/实际贷款
//            variables.put(PROCESS_VARIABLE_LOAN_AMOUNT_EXPECT, expectLoanAmount);
//            variables.put(PROCESS_VARIABLE_LOAN_AMOUNT_ACTUAL, actualLoanAmount);

                // 预计 < 13W, 但实际 >= 13W
                if (TASK_PROCESS_INIT.equals(loanProcessDO.getSocialCreditRecord()) && actualLoanAmount >= ACTUAL_LOAN_AMOUNT_13W) {

                    // 社会征信记录
                    List<LoanCreditInfoDO> socialCreditInfoDOS = loanCreditInfoDOMapper.getByCustomerIdAndType(loanOrderDO.getLoanCustomerId(), CREDIT_TYPE_SOCIAL);

                    // 没录过[社会征信]
                    if (CollectionUtils.isEmpty(socialCreditInfoDOS)) {

                        // target -> 补充生成 [社会征信录入]
                        variables.put(PROCESS_VARIABLE_TARGET, SOCIAL_CREDIT_RECORD.getCode());
                    }

                }

                // 是否为【打回】
                else if (TASK_PROCESS_REJECT.equals(loanProcessDO.getLoanApply())) {

                    // 打回记录
                    LoanRejectLogDO loanRejectLogDO = loanRejectLogDOMapper.lastByOrderIdAndTaskDefinitionKey(approval.getOrderId(), taskDefinitionKey);
                    Preconditions.checkNotNull(loanRejectLogDO, "[贷款申请-打回记录]丢失");

                    // [打回] -> 自于【资料审核】
                    if (MATERIAL_REVIEW.getCode().equals(loanRejectLogDO.getRejectOriginTask())) {

                        // target  -> [资料审核]
                        variables.put(PROCESS_VARIABLE_TARGET, MATERIAL_REVIEW.getCode());

                    }
                    // ELSE：[打回] -> 自于【电审】
                    else {

                        // target  -> filter
                        variables.put(PROCESS_VARIABLE_TARGET, LOAN_APPLY_VISIT_VERIFY_FILTER.getCode());
                    }

                }

                // 都不是，则
                else {

                    // target  -> filter
                    variables.put(PROCESS_VARIABLE_TARGET, LOAN_APPLY_VISIT_VERIFY_FILTER.getCode());
                }

            }

            // 2 || -1
            else if (ACTION_CANCEL.equals(action) || ACTION_REJECT_AUTO.equals(action)) {

                // target  -> filter
                variables.put(PROCESS_VARIABLE_TARGET, LOAN_APPLY_VISIT_VERIFY_FILTER.getCode());
            }

        }

        // 【电审】
        else if (TELEPHONE_VERIFY.getCode().equals(taskDefinitionKey)) {

            // PASS
            if (ACTION_PASS.equals(action)) {
                // 如果为打回
                if (TASK_PROCESS_REJECT.equals(loanProcessDO.getLoanApply())) {
                    LoanRejectLogDO loanRejectLogDO = loanRejectLogDOMapper.lastByOrderIdAndTaskDefinitionKey(approval.getOrderId(), TELEPHONE_VERIFY.getCode());
                    if (null != loanRejectLogDO) {
                        // 【金融方案申请】(自动)打回
                        if (BUSINESS_REVIEW.getCode().equals(loanRejectLogDO.getRejectOriginTask()) || LOAN_REVIEW.getCode().equals(loanRejectLogDO.getRejectOriginTask())) {
                            variables.put(PROCESS_VARIABLE_TARGET, BUSINESS_REVIEW.getCode());
                        }
                    }
                }
            }

            // REJECT
            else if (ACTION_REJECT_MANUAL.equals(action)) {
                variables.put(PROCESS_VARIABLE_TARGET, LOAN_APPLY.getCode());
            }
        }

        // [社会征信] & [PASS] &   [loan_apply 补充 [社会征信]]
        else if (SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey)) {

            // PASS
            if (ACTION_PASS.equals(action)) {

                if (TASK_PROCESS_DONE.equals(loanProcessDO.getLoanApply())) {

                    variables.put(PROCESS_VARIABLE_TARGET, LOAN_APPLY_VISIT_VERIFY_FILTER.getCode());
                } else {

                    variables.put(PROCESS_VARIABLE_TARGET, BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode());
                }
            }

            // REJECT
            else if (ACTION_REJECT_MANUAL.equals(action) || ACTION_REJECT_AUTO.equals(action)) {

                variables.put(PROCESS_VARIABLE_TARGET, BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode());
            }

        }

        // [合同套打]
        else if (MATERIAL_PRINT_REVIEW.getCode().equals(taskDefinitionKey) && ACTION_PASS.equals(action)) {

            // [合同归档]是否已存在
            Byte materialManageStatus = loanProcessDO.getMaterialManage();
            // 是否走了另一条线
            boolean hasMaterialManage = TASK_PROCESS_TODO.equals(materialManageStatus)
                    || TASK_PROCESS_DONE.equals(materialManageStatus)
                    || TASK_PROCESS_REJECT.equals(materialManageStatus);

            if (hasMaterialManage) {

                // 已走过一次[合同归档]
                variables.put(PROCESS_VARIABLE_TARGET, DATA_FLOW_CONTRACT_C2B.getCode());

            } else {

                // 银行匹配
                boolean is_match_condition_bank = tel_verify_match_condition_bank(loanOrderDO.getLoanBaseInfoId());

                if (is_match_condition_bank) {

                    // 走[申请分期]
                    variables.put(PROCESS_VARIABLE_TARGET, StringUtils.EMPTY);

                } else {

                    // 不走[申请分期]    target='usertask_material_manage&usertask_data_flow_contract_c2b'
                    variables.put(PROCESS_VARIABLE_TARGET, MATERIAL_MANAGE.getCode() + "&" + DATA_FLOW_CONTRACT_C2B.getCode());
                }

            }
        }

        // [004-合同资料公司至银行-确认接收]
        else if (DATA_FLOW_CONTRACT_C2B_REVIEW.getCode().equals(taskDefinitionKey) && ACTION_PASS.equals(action)) {

            // [005-抵押资料合伙人至公司]是否已存在
            Byte dataFlowMortgageP2cStatus = loanProcessDO.getDataFlowMortgageP2c();

            // 是否走了另一条线    -> 新建
            boolean otherFlow = TASK_PROCESS_TODO.equals(dataFlowMortgageP2cStatus)
                    || TASK_PROCESS_DONE.equals(dataFlowMortgageP2cStatus)
                    || TASK_PROCESS_REJECT.equals(dataFlowMortgageP2cStatus);

            // 资料流转 - 含抵押资料为：否 时，只走001-004，013-016
            boolean hasMortgageContract = hasMortgageContract(loanOrderDO.getMaterialAuditId());

            // 是否走了另一条线 || 抵押资料为：否
            if (otherFlow || !hasMortgageContract) {
                variables.put(PROCESS_VARIABLE_TARGET, BANK_LEND_RECORD.getCode());
            } else {
                // nothing
                variables.put(PROCESS_VARIABLE_TARGET, StringUtils.EMPTY);
            }
        }

        // [008-抵押资料公司至银行-确认接收]
        else if (DATA_FLOW_MORTGAGE_C2B_REVIEW.getCode().equals(taskDefinitionKey) && ACTION_PASS.equals(action)) {

            // 是否从前面的流程节点走过来的：  No.1 -> [009-抵押资料银行至公司]
            Byte dataFlowMortgageB2cStatus = loanProcessDO.getDataFlowMortgageB2c();

            // 是否走了另一条线    -> 新建
            boolean notOtherFlow = TASK_PROCESS_DONE.equals(dataFlowMortgageB2cStatus);

            // 是否走了另一条线
            if (notOtherFlow) {
                // 否
                variables.put(PROCESS_VARIABLE_TARGET, StringUtils.EMPTY);
            } else {
                // 是
                variables.put(PROCESS_VARIABLE_TARGET, DATA_FLOW_MORTGAGE_B2C.getCode());
            }
        }
    }

    /**
     * 是否含抵押资料
     *
     * @param materialAuditId
     * @return
     */
    private boolean hasMortgageContract(Long materialAuditId) {

        boolean hasMortgageContract = false;

        if (null != materialAuditId) {

            MaterialAuditDO materialAuditDO = materialAuditDOMapper.selectByPrimaryKey(materialAuditId);

            if (null != materialAuditDO) {

                Byte hasMortgageContract_val = materialAuditDO.getHasMortgageContract();

                hasMortgageContract = K_YORN_YES.equals(hasMortgageContract_val) ? true : false;
            }

        }

        return hasMortgageContract;
    }


    /**
     * 是否为：银行&社会征信并行任务   大于13W
     *
     * @param variables
     * @param taskDefinitionKey
     * @return
     */
    public boolean isBankAndSocialCreditRecordTask(Map<String, Object> variables, String taskDefinitionKey) {
        Byte expectLoanAmount = (Byte) variables.get(PROCESS_VARIABLE_LOAN_AMOUNT_EXPECT);
        boolean isBankAndSocialCreditRecordTask = (BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey) || SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey))
                && null != expectLoanAmount && expectLoanAmount >= EXPECT_LOAN_AMOUNT_EQT_13W_LT_20W;
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
        Byte expectLoanAmount = (Byte) variables.get(PROCESS_VARIABLE_LOAN_AMOUNT_EXPECT);
        boolean isOnlyOneBankCreditRecordTask = BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey)
                && EXPECT_LOAN_AMOUNT_LT_13W.equals(expectLoanAmount);
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
                && !MATERIAL_REVIEW.getCode().equals(variables.get(PROCESS_VARIABLE_TARGET));
        return isLoanApplyVisitVerifyFilterTask;
    }

    /**
     * 并行任务 -通过
     *
     * @param currentTask
     * @param tasks
     */
    private void dealCreditRecordTask_pass(Task currentTask, List<Task> tasks) {

        // 是否都通过了    -> 既非BANK，也非SOCIAL
        if (!CollectionUtils.isEmpty(tasks)) {

            long bank_social_count = tasks.stream()
                    .filter(Objects::nonNull)
                    // BANK || SOCIAL
                    .filter(e -> BANK_CREDIT_RECORD.getCode().equals(e.getTaskDefinitionKey())
                            || SOCIAL_CREDIT_RECORD.getCode().equals(e.getTaskDefinitionKey()))
                    .count();

            long bank_social_filter_count = tasks.stream()
                    .filter(Objects::nonNull)
                    // 只能为 BANK_SOCIAL_FILTER
                    .filter(e -> BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode().equals(e.getTaskDefinitionKey()))
                    .count();

            // 是 -> 放行
            if (bank_social_count == 0 && (bank_social_filter_count == 1 || bank_social_filter_count == 2)) {

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
                                loanProcessApprovalCommonService.completeTask(task.getId(), passVariables);

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
     * [银行&社会-征信] 并行任务 -打回
     *
     * @param currentTask
     * @param tasks
     */
    private void dealCreditRecordTask_reject(Task currentTask, List<Task> tasks) {

        // 打回 -> 结束掉其他子任务，然后打回
        if (!CollectionUtils.isEmpty(tasks)) {

            // 仅保留一个子任务  当做 -> 主任务
            final Task[] onlyAsMainTask = {null};

            // 其他子任务全部弃掉
            if (!CollectionUtils.isEmpty(tasks)) {

                Map<String, Object> cancelVariables = Maps.newHashMap();

                tasks.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> BANK_CREDIT_RECORD.getCode().equals(e.getTaskDefinitionKey())
                                || SOCIAL_CREDIT_RECORD.getCode().equals(e.getTaskDefinitionKey())
                                || BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode().equals(e.getTaskDefinitionKey())
                        )
                        .forEach((Task task) -> {

                            if (currentTask.getExecutionId().equals(task.getExecutionId())) {
                                // 拿到当前子任务
                                onlyAsMainTask[0] = task;

                            } else {

                                // 子任务 -> 弃单
                                cancelVariables.put(PROCESS_VARIABLE_ACTION, ACTION_CANCEL);
                                taskService.complete(task.getId(), cancelVariables);
                            }

                        });
            }

            // "主任务"  ->  打回
            Task currentFilterTask = onlyAsMainTask[0];
            Map<String, Object> rejectVariables = Maps.newHashMap();
            rejectVariables.put(PROCESS_VARIABLE_ACTION, ACTION_REJECT_MANUAL);
            loanProcessApprovalCommonService.completeTask(currentFilterTask.getId(), rejectVariables);
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
                passVariables.put(PROCESS_VARIABLE_REJECT_ORIGIN_TASK, null);
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

            // "主任务"  ->  自动打回 --> [征信申请]
            Task currentFilterTask = onlyAsMainTask[0];
            Map<String, Object> autuRejectVariables = Maps.newHashMap();
            autuRejectVariables.put(PROCESS_VARIABLE_ACTION, ACTION_REJECT_AUTO);
            autuRejectVariables.put(PROCESS_VARIABLE_TARGET, CREDIT_APPLY.getCode());
            taskService.complete(currentFilterTask.getId(), autuRejectVariables);
        }
    }

    /**
     * 弃单 -> 直接终止所有流程 => 所有运行中的act_ru_task
     *
     * @param processInstanceId
     */
    private void dealCancelTask(String processInstanceId) {
        List<Task> currentTaskList = loanProcessApprovalCommonService.getCurrentTaskList(processInstanceId);
        if (!CollectionUtils.isEmpty(currentTaskList)) {
            // 弃单 -> 直接终止所有流程 => 所有运行中的act_ru_task
            runtimeService.deleteProcessInstance(processInstanceId, "弃单");
        }
    }

    /**
     * 执行当前节点-附带任务
     *
     * @param approval
     * @param loanOrderDO
     * @param loanProcessDO
     */
    private void doCurrentNodeAttachTask(ApprovalParam approval, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        // 附带任务-[征信申请]
        doAttachTask_creditApply(approval, loanOrderDO);

        // 附带任务-[银行征信]
        doAttachTask_BankCreditRecord(approval, loanOrderDO);

        // 附带任务-[社会征信]
        doAttachTask_SocialCreditRecord(approval, loanOrderDO);

        // 附带任务-[贷款申请]
        doAttachTask_loanApply(approval, loanOrderDO, loanProcessDO);

        // 附带任务-[打款确认]
        doAttachTask_RemitReview(approval, loanOrderDO);

        // 附带任务-[金融方案修改-审核]
        doAttachTask_FinancialSchemeModifyApplyReview(approval, loanProcessDO);

        // 附带任务-[银行放款记录]
        doAttachTask_BankLendRecord(approval, loanOrderDO);

        // 附带任务-[弃单]
        doAttachTask_CancelTask(approval, loanOrderDO);
    }


    //1大于，2小于，3大于等于，4小于等于
    public void compardNum(String flag, BigDecimal now, BigDecimal data, String reason) {
        int i = 0;
        i++;
        if ("1".equals(flag)) {
            if (now.compareTo(data) != 1) {
                throw new BizException(reason + "不能小于等于" + data);
            }
        } else if ("2".equals(flag)) {
            if (now.compareTo(data) != -1) {
                throw new BizException(reason + "不能大于等于" + data);
            }
        } else if ("3".equals(flag)) {
            if (now.compareTo(data) == -1) {
                throw new BizException(reason + "不能小于" + data);
            }
        } else if ("4".equals(flag)) {
            if (now.compareTo(data) == 1) {
                throw new BizException(reason + "不能大于" + data);
            }
        }
    }


    /**
     * 附带任务-[贷款申请]
     *
     * @param approval
     * @param loanOrderDO
     * @param loanProcessDO
     */
    private void doAttachTask_loanApply(ApprovalParam approval, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        if (LOAN_APPLY.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {

            // 1、贷款申请校验1大于，2小于，3大于等于，4小于等于
            Map map = financialProductDOMapper.selectProductInfoByOrderId(loanOrderDO.getId());
            Long loanFinancialPlanId = loanOrderDOMapper.getLoanFinancialPlanIdById(loanOrderDO.getId());
            LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanFinancialPlanId);

            //金融手续费
            BigDecimal financialServiceFee = loanFinancialPlanDO.getBankPeriodPrincipal().subtract(loanFinancialPlanDO.getLoanAmount());
            //首付比例
            BigDecimal downPaymentRatio = loanFinancialPlanDO.getDownPaymentRatio();
            //贷款比例
            BigDecimal loanRate = loanFinancialPlanDO.getLoanAmount().divide(loanFinancialPlanDO.getCarPrice(), 2, BigDecimal.ROUND_HALF_UP);
            //银行分期比例
            BigDecimal stagingRatio = (BigDecimal) map.get("stagingRatio");

            LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
            String bankName = loanBaseInfoDO.getBank();

            LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
            int carType = loanCarInfoDO.getCarType();
            ConfLoanApplyDOKey confLoanApplyDOKey = new ConfLoanApplyDOKey();
            confLoanApplyDOKey.setBank(bankName);
            confLoanApplyDOKey.setCar_type(carType);

            ConfLoanApplyDO confLoanApplyDO = confLoanApplyDOMapper.selectByPrimaryKey(confLoanApplyDOKey);
            if (confLoanApplyDO != null) {
                if (confLoanApplyDO.getDown_payment_ratio() != null && confLoanApplyDO.getDown_payment_ratio_compare() != null) {
                    compardNum(confLoanApplyDO.getDown_payment_ratio_compare(), downPaymentRatio, confLoanApplyDO.getDown_payment_ratio(), "首付比例");
                }
                if (confLoanApplyDO.getFinancial_service_fee() != null && confLoanApplyDO.getFinancial_service_fee_compard() != null) {
                    compardNum(confLoanApplyDO.getFinancial_service_fee_compard(), financialServiceFee.divide(new BigDecimal("10000")), confLoanApplyDO.getFinancial_service_fee(), "金融手续费1");
                }
                if (confLoanApplyDO.getCar_ratio() != null && confLoanApplyDO.getCar_ratio_compard() != null) {
                    compardNum(confLoanApplyDO.getCar_ratio_compard(), financialServiceFee, loanFinancialPlanDO.getCarPrice().multiply(confLoanApplyDO.getCar_ratio().divide(new BigDecimal("100"))), "金融手续费2");
                }
                if (confLoanApplyDO.getLoan_ratio() != null && confLoanApplyDO.getLoan_ratio_compare() != null) {
                    compardNum(confLoanApplyDO.getLoan_ratio_compare(), loanRate, confLoanApplyDO.getLoan_ratio().divide(new BigDecimal("100")), "贷款比例");
                }
                if (confLoanApplyDO.getStaging_ratio() != null && confLoanApplyDO.getStaging_ratio_compard() != null) {
                    compardNum(confLoanApplyDO.getStaging_ratio_compard(), stagingRatio, confLoanApplyDO.getStaging_ratio(), "银行分期比例");
                }
            }

            // 2、贷款申请提交后，提交视频面签登记
            if (TASK_PROCESS_TODO.equals(loanProcessDO.getLoanInfoRecord())
                    || TASK_PROCESS_REJECT.equals(loanProcessDO.getLoanInfoRecord())) {

                ApprovalParam approvalParam = new ApprovalParam();
                approvalParam.setOrderId(approval.getOrderId());
                approvalParam.setTaskDefinitionKey(LOAN_INFO_RECORD.getCode());
                approvalParam.setAction(TASK_PROCESS_DONE);

                approvalParam.setNeedLog(false);
                approvalParam.setAutoTask(true);
                approvalParam.setNeedPush(false);
                approvalParam.setCheckPermission(false);

                approval(approvalParam);
            }
        }
    }

    /**
     * 附带任务-[弃单]
     *
     * @param approval
     * @param loanOrderDO
     */

    private void doAttachTask_CancelTask(ApprovalParam approval, LoanOrderDO loanOrderDO) {

        // 弃单 -> 直接终止所有流程 => 所有运行中的act_ru_task
        if (ACTION_CANCEL.equals(approval.getAction())) {
            dealCancelTask(loanOrderDO.getProcessInstId());
        }
    }

    /**
     * 附带任务-[社会征信]
     *
     * @param approval
     * @param loanOrderDO
     */
    private void doAttachTask_SocialCreditRecord(ApprovalParam approval, LoanOrderDO loanOrderDO) {

        // 社会征信
        if (SOCIAL_CREDIT_RECORD.getCode().equals(approval.getTaskDefinitionKey())) {

            // PASS
            if (ACTION_PASS.equals(approval.getAction())) {

                // 记录客户社会征信查询历史记录 -- 提交时间/人
                loanCreditInfoHisService.saveCreditInfoHis_SocialCreditRecord(loanOrderDO.getLoanCustomerId());
            }

            // 打回
            else if (ACTION_REJECT_MANUAL.equals(approval.getAction()) || ACTION_REJECT_AUTO.equals(approval.getAction())) {

                // 记录客户社会征信查询历史记录 -- 打回时间/人
                loanCreditInfoHisService.saveCreditInfoHis_SocialCreditReject(loanOrderDO.getLoanCustomerId(), approval.getInfo(), approval.isAutoTask());
            }
        }
    }

    /**
     * 附带任务-[征信申请]
     *
     * @param approval
     * @param loanOrderDO
     */
    private void doAttachTask_creditApply(ApprovalParam approval, LoanOrderDO loanOrderDO) {

        // 征信申请 && PASS
        if (CREDIT_APPLY.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {

            // 通过银行接口  ->  自动查询银行征信
            bankSolutionService.creditAutomaticCommit(approval.getOrderId());

            // 创建征信查询历史记录  --> 银行/社会
            loanCreditInfoHisService.saveCreditInfoHis_CreditApply(loanOrderDO.getLoanCustomerId(), getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId()).getLoanAmount());
        }
    }

    /**
     * 附带任务-[银行征信]
     *
     * @param approval
     * @param loanOrderDO
     */
    private void doAttachTask_BankCreditRecord(ApprovalParam approval, LoanOrderDO loanOrderDO) {

        // 银行征信
        if (BANK_CREDIT_RECORD.getCode().equals(approval.getTaskDefinitionKey())) {

            // PASS
            if (ACTION_PASS.equals(approval.getAction())) {

                // 记录客户银行征信查询历史记录 -- 提交时间/人
                loanCreditInfoHisService.saveCreditInfoHis_BankCreditRecord(loanOrderDO.getLoanCustomerId());
            }

            // 打回
            else if (ACTION_REJECT_MANUAL.equals(approval.getAction()) || ACTION_REJECT_AUTO.equals(approval.getAction())) {

                // 记录客户银行征信查询历史记录 -- 打回时间/人
                loanCreditInfoHisService.saveCreditInfoHis_BankCreditReject(loanOrderDO.getLoanCustomerId(), approval.getInfo(), approval.isAutoTask());
            }
        }
    }

    /**
     * 附带任务-[打款确认]
     *
     * @param approval
     * @param loanOrderDO
     */
    private void doAttachTask_RemitReview(ApprovalParam approval, LoanOrderDO loanOrderDO) {

        if (REMIT_REVIEW.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {

            // 1、打款时间
            RemitDetailsDO remitDetailsDO = new RemitDetailsDO();
            remitDetailsDO.setId(loanOrderDO.getRemitDetailsId());
            remitDetailsDO.setRemit_time(new Date());
            int count = remitDetailsDOMapper.updateByPrimaryKeySelective(remitDetailsDO);
            Preconditions.checkArgument(count > 0, "编辑失败");

            // 2、自动启动流程 -> [第三方过桥资金]   -杭州城站
            LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());
            if (BANK_NAME_ICBC_HangZhou_City_Station_Branch.equals(loanBaseInfoDO.getBank())) {

                LoanProcessBridgeDO loanProcessBridgeDO = loanProcessBridgeDOMapper.selectByOrderId(loanOrderDO.getId());

                if (loanProcessBridgeDO == null) {
                    Long startProcessId = loanProcessBridgeService.startProcess(approval.getOrderId());

                    // 绑定当前流程到金投行
                    ConfThirdRealBridgeProcessDO thirdRealBridgeProcessDO = new ConfThirdRealBridgeProcessDO();
                    thirdRealBridgeProcessDO.setBridgeProcessId(startProcessId);
                    thirdRealBridgeProcessDO.setConfThirdPartyId(IDict.K_CONF_THIRD_PARTY.K_JTH);
                    int insertCount = confThirdRealBridgeProcessDOMapper.insert(thirdRealBridgeProcessDO);
                    Preconditions.checkArgument(insertCount > 0, "插入失败");
                }
            }
        }
    }

    /**
     * 附带任务-银行打款更新进投行还款
     */
    private void doAttachTask_BankLendRecord(ApprovalParam approval, LoanOrderDO loanOrderDO) {
        if (BANK_LEND_RECORD.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {
            LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());
            if (BANK_NAME_ICBC_HangZhou_City_Station_Branch.equals(loanBaseInfoDO.getBank())) {
                LoanProcessBridgeDO loanProcessBridgeDO = loanProcessBridgeDOMapper.selectByOrderId(loanOrderDO.getId());
                if (loanProcessBridgeDO != null) {
                    if (loanProcessBridgeDO.getBridgeRepayRecord() == 2) {
                        CalMoneyVO calMoneyVO = calBankLendRecord(loanProcessBridgeDO.getId(), loanProcessBridgeDO.getOrderId());
                        thirdPartyFundBusinessDOMapper.updateInfo(loanOrderDO.getId(), DateUtil.getDate10(calMoneyVO.getBankDate()), new BigDecimal(calMoneyVO.getInterest()), new BigDecimal(calMoneyVO.getPoundage()));
                    }
                }
            }
        }

    }

    public CalMoneyVO calBankLendRecord(Long bridgeProcessId, Long orderId) {
        CalMoneyVO calMoneyVO = new CalMoneyVO();
        BigDecimal yearRate;
        BigDecimal singleRate;
        BigDecimal lend_amount;
        Date lendDate;
        Date repayDate;
        int timeNum;

        Long conf_third_party_id;
        ConfThirdRealBridgeProcessDO confThirdRealBridgeProcessDO = confThirdRealBridgeProcessDOMapper.selectByPrimaryKey(bridgeProcessId);
        if (confThirdRealBridgeProcessDO != null) {
            conf_third_party_id = confThirdRealBridgeProcessDO.getConfThirdPartyId();
            ConfThirdPartyMoneyDO confThirdPartyMoneyDO = confThirdPartyMoneyDOMapper.selectByPrimaryKey(conf_third_party_id);
            yearRate = confThirdPartyMoneyDO.getYearRate();
            singleRate = confThirdPartyMoneyDO.getSingleRate();
            ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);
            lendDate = thirdPartyFundBusinessDO.getLendDate();
            BankLendRecordDO bankLendRecordDO = bankLendRecordDOMapper.selectByLoanOrder(orderId);
            repayDate = bankLendRecordDO.getLendDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (bankLendRecordDO != null) {
                if (repayDate != null) {

                    Calendar c = Calendar.getInstance();
                    c.setTime(bankLendRecordDO.getLendDate());
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    repayDate = c.getTime();
                }
            }
            timeNum = (int) ((repayDate.getTime() - lendDate.getTime()) / (1000 * 3600 * 24));
            lend_amount = thirdPartyFundBusinessDO.getLendAmount();
            if (lend_amount != null) {
                lend_amount = new BigDecimal("0.00");
            }
            calMoneyVO.setInterest(String.valueOf(yearRate.divide(BigDecimal.valueOf(100)).multiply(lend_amount).multiply(BigDecimal.valueOf(timeNum)).divide(BigDecimal.valueOf(365), 2, BigDecimal.ROUND_HALF_UP)));
            calMoneyVO.setPoundage(String.valueOf(singleRate.divide(BigDecimal.valueOf(100)).multiply(lend_amount).multiply(BigDecimal.valueOf(timeNum)).divide(BigDecimal.valueOf(365), 2, BigDecimal.ROUND_HALF_UP)));
            calMoneyVO.setBankDate(sdf.format(repayDate));
        }
        return calMoneyVO;
    }

    /**
     * 附带任务-[金融方案修改-审核]
     *
     * @param approval
     * @param loanProcessDO
     */
    private void doAttachTask_FinancialSchemeModifyApplyReview(ApprovalParam approval, LoanProcessDO loanProcessDO) {

        if (FINANCIAL_SCHEME_MODIFY_APPLY_REVIEW.getCode().equals(approval.getTaskDefinitionKey())
                && ACTION_PASS.equals(approval.getAction())) {

            // 1、生成[视频审核]待办
            Byte videoReviewStatus = loanProcessDO.getVideoReview();
            if (TASK_PROCESS_DONE.equals(videoReviewStatus)) {

                ApprovalParam param = new ApprovalParam();

                param.setOrderId(approval.getOrderId());
                param.setTaskDefinitionKey(VIDEO_REVIEW.getCode());
                param.setAction(ACTION_ROLL_BACK);
                param.setInfo("金融方案修改");

                param.setCheckPermission(false);
                param.setNeedLog(true);
                param.setNeedPush(true);

                ResultBean<Void> approvalResult = approval(param);
                Preconditions.checkArgument(approvalResult.getSuccess(), approvalResult.getMsg());
            }
        }
    }
}
