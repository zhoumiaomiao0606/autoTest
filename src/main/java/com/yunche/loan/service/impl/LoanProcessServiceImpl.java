package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.constant.VideoFaceConst;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateTimeFormatUtils;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.config.util.StringUtil;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.LoanProcessLogVO;
import com.yunche.loan.domain.vo.LoanRejectLogVO;
import com.yunche.loan.domain.vo.TaskStateVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.ApplyOrderStatusConst.*;
import static com.yunche.loan.config.constant.BankConst.BANK_NAME_ICBC_HangZhou_City_Station_Branch;
import static com.yunche.loan.config.constant.BankConst.BANK_NAME_ICBC_TaiZhou_LuQiao_Branch;
import static com.yunche.loan.config.constant.BaseConst.K_YORN_NO;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.CarConst.CAR_KEY_FALSE;
import static com.yunche.loan.config.constant.LoanCustomerConst.CREDIT_TYPE_SOCIAL;
import static com.yunche.loan.config.constant.LoanCustomerConst.CUST_ID_CARD_EXPIRE_DATE;
import static com.yunche.loan.config.constant.LoanCustomerConst.CUST_TYPE_EMERGENCY_CONTACT;
import static com.yunche.loan.config.constant.LoanAmountConst.*;
import static com.yunche.loan.config.constant.LoanDataFlowConst.DATA_FLOW_TASK_KEY_PREFIX;
import static com.yunche.loan.config.constant.LoanDataFlowConst.DATA_FLOW_TASK_KEY_REVIEW_SUFFIX;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.*;
import static com.yunche.loan.config.constant.LoanRefundApplyConst.REFUND_REASON_3;
import static com.yunche.loan.config.constant.LoanUserGroupConst.*;
import static com.yunche.loan.config.constant.TaskDistributionConst.TASK_STATUS_DOING;
import static com.yunche.loan.config.constant.TaskDistributionConst.TASK_STATUS_DONE;
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
    private VideoFaceLogDOMapper videoFaceLogDOMapper;

    @Autowired
    private TaskDistributionDOMapper taskDistributionDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private ActivitiDeploymentMapper activitiDeploymentMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private JpushService jpushService;

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
    private LoanDataFlowService loanDataFlowService;

    @Autowired
    private DictService dictService;


    @Override
    @Transactional
    public ResultBean<Void> approval(ApprovalParam approval) {
        Preconditions.checkNotNull(approval.getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(approval.getAction(), "审核结果不能为空");

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

        // 业务单
        LoanOrderDO loanOrderDO = getLoanOrder(approval.getOrderId());

        // 节点实时状态
        LoanProcessDO loanProcessDO = getLoanProcess(approval.getOrderId());

        // 贷款基本信息
        LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());

        // 校验审核前提条件
        checkPreCondition(approval.getTaskDefinitionKey(), approval.getAction(), loanOrderDO, loanProcessDO);

        // 日志
        log(approval);


        ////////////////////////////////////////// ↓↓↓↓↓ 特殊处理  ↓↓↓↓↓ ////////////////////////////////////////////////

        // 【征信增补】
        if (isCreditSupplementTask(approval.getTaskDefinitionKey(), approval.getAction())) {
            execCreditSupplementTask(approval, loanOrderDO.getProcessInstId(), loanProcessDO);
        }

        // 【资料流转（抵押资料 - 合伙人->公司）】
        if (isDataFlowMortgageP2cNewTask(approval.getTaskDefinitionKey(), approval.getAction())) {
            execDataFlowMortgageP2cNewFilterTask(approval);
        }


        // 【贷款信息登记】
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
            return execRefundApplyTask(approval, loanProcessDO);
        }

        // 【财务报销】
        if (isOutworkerCostApplyTask(approval.getTaskDefinitionKey())) {
            return execOutworkerCostApplyTask(approval);
        }

        // 【反审】
        if (actionIsRollBack(approval.getAction())) {
            return execRollBackTask(approval, loanOrderDO, loanProcessDO);
        }

        ////////////////////////////////////////// ↑↑↑↑↑ 特殊处理  ↑↑↑↑↑ ////////////////////////////////////////////////


        // 获取当前执行任务（activiti中）
        Task task = getTask(loanOrderDO.getProcessInstId(), approval.getTaskDefinitionKey());

        // 先获取提交之前的待执行任务ID列表
        List<String> startTaskIdList = getCurrentTaskIdList(task.getProcessInstanceId());

        // 流程变量
        Map<String, Object> variables = setAndGetVariables(task, approval, loanOrderDO, loanProcessDO);

        // 执行任务
        execTask(task, variables, approval, loanOrderDO, loanProcessDO);

        // 流程数据同步
        syncProcess(startTaskIdList, loanOrderDO.getProcessInstId(), approval, loanProcessDO, loanBaseInfoDO);

        // 生成客户还款计划
        createRepayPlan(approval.getTaskDefinitionKey(), loanProcessDO, loanOrderDO);

        // [领取]完成
        finishTask(approval, startTaskIdList, loanOrderDO.getProcessInstId());

        // 通过银行接口  ->  自动查询征信
        creditAutomaticCommit(approval);

        // 异步打包文件
        asyncPackZipFile(approval.getTaskDefinitionKey(), loanProcessDO, 2);

        // 异步推送
        asyncPush(loanOrderDO.getId(), loanOrderDO.getLoanBaseInfoId(), approval.getTaskDefinitionKey(), approval);

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
     * 执行 - [贷款信息登记]任务
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

                updateLoanProcess(loanProcessDO);
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
     * 执行  -->   自动提交  （重复）新产生的[贷款信息登记]
     *
     * @param loanInfoRecordTaskList
     */
    private void doAutoCompleteLoanInfoRecordTask(List<Task> loanInfoRecordTaskList) {

        if (CollectionUtils.isEmpty(loanInfoRecordTaskList)) {
            return;
        }

        Map<String, Object> passVariables = Maps.newHashMap();
        passVariables.put(PROCESS_VARIABLE_ACTION, ACTION_PASS);

        // 剩下的（新产生的）  ==>  全直接在activiti中完成
        loanInfoRecordTaskList.stream()
                .forEach(task -> {

                    // 在activiti中完成
                    completeTask(task.getId(), passVariables);
                });
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
     * 执行[反审]
     *
     * @param approval
     * @param loanOrderDO
     * @param loanProcessDO
     * @return
     */
    private ResultBean<Void> execRollBackTask(ApprovalParam approval, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO) {

        // [业务申请]
        if (LOAN_APPLY.getCode().equals(approval.getTaskDefinitionKey())) {

            // [业务申请] 必须已提交
            Byte loanApplyStatus = loanProcessDO.getLoanApply();
            Preconditions.checkArgument(TASK_PROCESS_DONE.equals(loanApplyStatus), "操作错误：[业务申请]还未提交,不能发起[反审]");

            // [电审]任务状态
            Byte telephoneVerifyStatus = loanProcessDO.getTelephoneVerify();

            // 1
            if (TASK_PROCESS_DONE.equals(telephoneVerifyStatus)) {
                throw new BizException("[电审]已完成，无法发起[业务申请-反审]");
            }

            // 3   -不存在该状态

            // 4、5、6、7
            else if (TASK_PROCESS_TELEPHONE_VERIFY_COMMISSIONER.equals(telephoneVerifyStatus)
                    || TASK_PROCESS_TELEPHONE_VERIFY_LEADER.equals(telephoneVerifyStatus)
                    || TASK_PROCESS_TELEPHONE_VERIFY_MANAGER.equals(telephoneVerifyStatus)
                    || TASK_PROCESS_TELEPHONE_VERIFY_DIRECTOR.equals(telephoneVerifyStatus)) {

                throw new BizException("[电审]任务已领取，无法发起[业务申请-反审]");
            }

            // 2     -[电审]中
            // 已流转到[电审]，但未被领取的时候，可进行-[反审]
            else if (TASK_PROCESS_TODO.equals(telephoneVerifyStatus)) {

                TaskDistributionDO taskDistributionDO = taskDistributionDOMapper.selectByPrimaryKey(approval.getOrderId(), TELEPHONE_VERIFY.getCode());

                if (null != taskDistributionDO) {
                    Preconditions.checkArgument(TASK_STATUS_DOING.equals(taskDistributionDO.getStatus()), "[电审]任务已领取，无法发起[业务申请-反审]");
                    Preconditions.checkArgument(TASK_STATUS_DONE.equals(taskDistributionDO.getStatus()), "[电审]任务已完成，无法发起[业务申请-反审]");
                }

                // [电审]-反审 自动打回至[贷款申请]和[上门调查]
                autoPassTask(approval.getOrderId(), TELEPHONE_VERIFY.getCode(), ACTION_REJECT_AUTO);

                // update process
                LoanProcessDO loanProcessDO_ = new LoanProcessDO();
                loanProcessDO_.setOrderId(approval.getOrderId());
                loanProcessDO_.setTelephoneVerify(TASK_PROCESS_INIT);
                loanProcessDO_.setLoanApply(TASK_PROCESS_TODO);
                loanProcessDO_.setVisitVerify(TASK_PROCESS_TODO);
                updateLoanProcess(loanProcessDO_);
            }

            // 0     -[电审]前   ==> 即：[上门调查]未提交
            else if (TASK_PROCESS_INIT.equals(telephoneVerifyStatus)) {

                // [业务申请] ->执行[反审]
                doLoanApplyVisitVerifyFilterTask_RollBack(loanOrderDO);

                // update process
                LoanProcessDO loanProcessDO_ = new LoanProcessDO();
                loanProcessDO_.setOrderId(approval.getOrderId());
                loanProcessDO_.setLoanApply(TASK_PROCESS_TODO);
                loanProcessDO_.setVisitVerify(TASK_PROCESS_TODO);
                updateLoanProcess(loanProcessDO_);

            } else {

                throw new BizException("[业务申请-反审]发起异常");
            }

        } else {

            throw new BizException("发起[反审]节点错误：" + LoanProcessEnum.getNameByCode(approval.getTaskDefinitionKey()));
        }

        return ResultBean.ofSuccess(null, "[反审]发起成功");
    }

    /**
     * 通过银行接口  ->  自动查询征信
     *
     * @param approval
     */
    private void creditAutomaticCommit(ApprovalParam approval) {

        // 征信申请 && PASS
        if (CREDIT_APPLY.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {
            bankSolutionService.creditAutomaticCommit(approval.getOrderId());
        }
    }

    /**
     * [领取]完成
     *
     * @param approval
     * @param startTaskIdList
     * @param processInstId
     */
    private void finishTask(ApprovalParam approval, List<String> startTaskIdList, String processInstId) {

        if (null != approval.getTaskId()) {

            // PASS
            if (ACTION_PASS.equals(approval.getAction())) {

                // pass-当前task
                taskDistributionService.finish(approval.getTaskId(), approval.getOrderId(), approval.getTaskDefinitionKey());

                // open-新产生的任务    如果新任务是：过去已存在(被打回过)，一律OPEN
                List<String> newTaskKeyList = getNewTaskKeyList(processInstId, startTaskIdList);
                // open-被打回过的Tasks
                taskDistributionService.rejectFinish(approval.getTaskId(), approval.getOrderId(), newTaskKeyList);
            }

            // REJECT
            else if (ACTION_REJECT_MANUAL.equals(approval.getAction()) || ACTION_REJECT_AUTO.equals(approval.getAction())) {

                List<String> newTaskKeyList = getNewTaskKeyList(processInstId, startTaskIdList);

                if (!CollectionUtils.isEmpty(newTaskKeyList)) {

                    // open-reject2Tasks
                    taskDistributionService.rejectFinish(approval.getTaskId(), approval.getOrderId(), newTaskKeyList);
                }
            }
        }
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
     * @param loanProcessDO
     * @param retryNum          重试次数
     */
    private void asyncPackZipFile(String taskDefinitionKey, LoanProcessDO loanProcessDO, Integer retryNum) {

        if (null == retryNum) {
            retryNum = 0;
        }
        //资料增补、电审、录入提车资料后，都会出发异步打包操作
        if (VEHICLE_INFORMATION.getCode().equals(taskDefinitionKey)
                || INFO_SUPPLEMENT.getCode().equals(taskDefinitionKey)
                || LOAN_APPLY.getCode().equals(taskDefinitionKey)
                || VISIT_VERIFY.getCode().equals(taskDefinitionKey)) {

            if (retryNum < 0) {
                return;
            }
            retryNum--;

            int finalRetryNum = retryNum;
            executorService.execute(new Runnable() {
                @Override
                public void run() {

                    ResultBean<String> resultBean = null;
                    try {
                        // 打包，并上传至OSS
                        resultBean = materialService.downloadFiles2OSS(loanProcessDO.getOrderId(), true);
                    } catch (Exception e) {
                        logger.error("asyncPackZipFile error", e);
                    } finally {
                        // 失败，重试
                        if (null == resultBean || !resultBean.getSuccess()) {
                            asyncPackZipFile(taskDefinitionKey, loanProcessDO, finalRetryNum);
                        }
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

        // 【金融方案修改申请】
        if (FINANCIAL_SCHEME_MODIFY_APPLY.getCode().equals(approval.getTaskDefinitionKey())) {
            // 提交
            Preconditions.checkArgument(ACTION_PASS.equals(approval.getAction()), "流程审核参数有误");

            // 操作锁定 (退款申请中/金融方案修改申请中);
            lockProcess(loanProcessDO);

            // 更新申请单状态
            updateFinancialSchemeModifyApply(approval, APPLY_ORDER_TODO);

            // [领取]完成
            finishTask_(approval);

            return ResultBean.ofSuccess(null, "[金融方案修改]任务执行成功");
        }

        // 【金融方案修改申请审核】
        else if (FINANCIAL_SCHEME_MODIFY_APPLY_REVIEW.getCode().equals(approval.getTaskDefinitionKey())) {

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
                updateLoanProcess(loanProcessDO);

            } else {
                throw new BizException("流程审核参数有误");
            }

            // [领取]完成
            finishTask_(approval);

            return ResultBean.ofSuccess(null, "[金融方案审核]任务执行成功");
        }

        return ResultBean.ofError("参数有误");
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
        updateLoanProcess(loanProcessDO);
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
     * 自动打回    - 放款审批 -> 业务审批
     *
     * @param loanProcessDO
     */
    private void autoRejectLoanReviewTask(LoanProcessDO loanProcessDO) {
        // 打回
        if (TASK_PROCESS_TODO.equals(loanProcessDO.getLoanReview())) {

            ApprovalParam approvalParam = new ApprovalParam();
            approvalParam.setOrderId(loanProcessDO.getOrderId());
            approvalParam.setTaskDefinitionKey(LOAN_REVIEW.getCode());
            approvalParam.setAction(ACTION_REJECT_MANUAL);
            approvalParam.setNeedLog(false);
            approvalParam.setCheckPermission(false);

            approval(approvalParam);
        }
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
        updateLoanProcess(loanProcessDO);
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
        loanFinancialPlanDO.setPrincipalInterestSum(loanFinancialPlanTempHisDO.getFinancial_bank_period_principal());

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
     * @param loanProcessDO
     * @return
     */
    private ResultBean<Void> execRefundApplyTask(ApprovalParam approval, LoanProcessDO loanProcessDO) {

        // 【退款申请】
        if (REFUND_APPLY.getCode().equals(approval.getTaskDefinitionKey())) {
            // 提交
            Preconditions.checkArgument(ACTION_PASS.equals(approval.getAction()), "流程审核参数有误");

            // check
            Preconditions.checkArgument(!TASK_PROCESS_INIT.equals(loanProcessDO.getLoanReview()), "[打款确认]未提交，无法发起退款申请");
            Preconditions.checkArgument(!TASK_PROCESS_TODO.equals(loanProcessDO.getLoanReview()), "[打款确认]未提交，无法发起退款申请");

            // 锁定操作 (退款申请中/金融方案修改申请中)
            lockProcess(loanProcessDO);

            // 更新申请单状态
            updateRefundApply(approval, APPLY_ORDER_TODO);

            return ResultBean.ofSuccess(null, "[退款申请]任务执行成功");
        }

        // 【退款申请审核】
        else if (REFUND_APPLY_REVIEW.getCode().equals(approval.getTaskDefinitionKey())) {

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
                updateLoanProcess(loanProcessDO);

            } else if (ACTION_REJECT_MANUAL.equals(approval.getAction())) {

                // 更新申请单状态
                updateRefundApply(approval, APPLY_ORDER_REJECT);

                // 打回记录
                createRejectLog_(loanProcessDO.getOrderId(), approval.getTaskDefinitionKey(), REFUND_APPLY.getCode(), approval.getInfo());

            } else {
                throw new BizException("流程审核参数有误");
            }

            return ResultBean.ofSuccess(null, "[退款申请审核]任务执行成功");
        }

        return ResultBean.ofError("参数有误");
    }

    /**
     * 执行 -【财务报销】
     *
     * @param approval
     * @param loanProcessDO
     * @return
     */
    private ResultBean<Void> execOutworkerCostApplyTask(ApprovalParam approval) {

        // [外勤费用申报]
        if (OUTWORKER_COST_APPLY.getCode().equals(approval.getTaskDefinitionKey())) {

            // PASS
            if (ACTION_PASS.equals(approval.getAction())) {

                updateOutworkerCostApplyProcess(approval, ApplyOrderStatus.APPLY_ORDER_DONE__APPLY_ORDER_REVIEW_TODO);
            }
        }

        // [财务报销]
        else if (OUTWORKER_COST_APPLY_REVIEW.getCode().equals(approval.getTaskDefinitionKey())) {

            Byte action = approval.getAction();

            // PASS
            if (ACTION_PASS.equals(action)) {

                updateOutworkerCostApplyProcess(approval, ApplyOrderStatus.APPLY_ORDER_REVIEW_PASS);
            }
            // REJECT
            else if (ACTION_REJECT_MANUAL.equals(action)) {

                updateOutworkerCostApplyProcess(approval, ApplyOrderStatus.APPLY_ORDER_REJECT);
            }
        }

        return ResultBean.ofSuccess(null);
    }

    private void updateOutworkerCostApplyProcess(ApprovalParam approval, Byte applyOrderStatus) {

        LegworkReimbursementDO legworkReimbursementDO = new LegworkReimbursementDO();
        legworkReimbursementDO.setId(approval.getSupplementOrderId());


        legworkReimbursementDO.setStatus(ORDER_STATUS_DOING);
        legworkReimbursementDO.setGmtUpdateTime(new Date());

        int count = legworkReimbursementDOMapper.updateByPrimaryKeySelective(legworkReimbursementDO);
        Preconditions.checkArgument(count > 0, "");
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
     * @param startTaskIdList      起始任务ID列表
     * @param processInstId
     * @param approval
     * @param currentLoanProcessDO
     * @param loanBaseInfoDO
     */
    private void syncProcess(List<String> startTaskIdList, String processInstId, ApprovalParam approval,
                             LoanProcessDO currentLoanProcessDO, LoanBaseInfoDO loanBaseInfoDO) {

        // 更新状态
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(approval.getOrderId());

        // 如果弃单，则记录弃单节点
        if (ACTION_CANCEL.equals(approval.getAction())) {
            loanProcessDO.setOrderStatus(ORDER_STATUS_CANCEL);
            loanProcessDO.setCancelTaskDefKey(approval.getTaskDefinitionKey());
            updateCurrentTaskProcessStatus(loanProcessDO, approval.getTaskDefinitionKey(), TASK_PROCESS_CANCEL, approval);
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
        } else if (ACTION_PASS.equals(approval.getAction()) && !TELEPHONE_VERIFY.getCode().equals(approval.getTaskDefinitionKey())) {
            // Tips：[电审]通过 状态更新不走这里
            taskProcessStatus = TASK_PROCESS_DONE;
        }
        updateCurrentTaskProcessStatus(loanProcessDO, approval.getTaskDefinitionKey(), taskProcessStatus, approval);

        // 更新新产生的任务状态
        updateNextTaskProcessStatus(loanProcessDO, processInstId, startTaskIdList, approval, currentLoanProcessDO);

        // 特殊处理：部分节点的同步  !!!
        special_syncProcess(approval, loanProcessDO, currentLoanProcessDO, loanBaseInfoDO);

        // 更新本地流程记录
        updateLoanProcess(loanProcessDO);
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

            // 中国工商银行杭州城站支行
            if (BANK_NAME_ICBC_HangZhou_City_Station_Branch.equals(loanBaseInfoDO.getBank())) {

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
        LoanOrderDO loanOrderDO = getLoanOrder(approval.getOrderId());

        // 日志
        log(approval);

        // activiti 弃单[结束流程]
        dealCancelTask(loanOrderDO.getProcessInstId());

        // 更新状态
        loanProcessDO.setOrderStatus(ORDER_STATUS_CANCEL);
        loanProcessDO.setCancelTaskDefKey(approval.getTaskDefinitionKey());

        updateLoanProcess(loanProcessDO);

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
            LocalDate idCardExpireDate = LocalDate.of(Integer.valueOf(expireDateStrArr[0]), Integer.valueOf(expireDateStrArr[1]), Integer.valueOf(expireDateStrArr[2]));

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

        // 【业务审批】|| 【放款审批】  -> PASS
        else if ((BUSINESS_REVIEW.getCode().equals(taskDefinitionKey) || LOAN_REVIEW.getCode().equals(taskDefinitionKey))
                && ACTION_PASS.equals(action)) {

            // 财务放款审批时，需要判断几个子业务的状态
            // 1.[金融方案修改申请]审批通过
            // 进行中的【金融方案修改申请】
            LoanFinancialPlanTempHisDO loanFinancialPlanTempHisDO = loanFinancialPlanTempHisDOMapper.lastByOrderId(loanOrderDO.getId());
            if (null != loanFinancialPlanTempHisDO) {
                Preconditions.checkArgument(APPLY_ORDER_PASS.equals(loanFinancialPlanTempHisDO.getStatus()), "当前订单已发起[金融方案修改申请]，请待审核通过后再操作！");
            }

            // 2.GPS安装完成
            LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
            Preconditions.checkNotNull(loanCarInfoDO, "车辆信息不存在");
            Integer gpsNum = loanCarInfoDO.getGpsNum();
            if (null != gpsNum && gpsNum > 0) {
                Preconditions.checkArgument(TASK_PROCESS_DONE.equals(loanProcessDO.getInstallGps()), "当前订单[GPS安装]未提交");
            }

            // 3.（根据银行配置）视频面签完成   -仅：台州路桥支行
            LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());
            if (BANK_NAME_ICBC_TaiZhou_LuQiao_Branch.equals(loanBaseInfoDO.getBank())) {
                VideoFaceLogDO videoFaceLogDO = videoFaceLogDOMapper.lastVideoFaceLogByOrderId(loanOrderDO.getId());
                Preconditions.checkNotNull(videoFaceLogDO, "当前订单未进行[视频面签]");
                Preconditions.checkArgument(VideoFaceConst.ACTION_PASS.equals(videoFaceLogDO.getAction()), "当前订单[视频面签]审核未通过");
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

        // [业务付款申请]
        else if (BUSINESS_PAY.getCode().equals(taskDefinitionKey)) {

            // PASS
            if (ACTION_PASS.equals(action)) {

                // 前置校验
                boolean is_match_condition_bank = tel_verify_match_condition_bank(loanOrderDO.getLoanBaseInfoId());

                if (is_match_condition_bank) {

                    // 若未开卡，提交业务付款申请单时候，提示：请先提交开卡申请
                    String lastBankInterfaceSerialStatus = loanQueryDOMapper.selectLastBankInterfaceSerialStatusByTransCode(loanOrderDO.getLoanCustomerId(), IDict.K_TRANS_CODE.CREDITCARDAPPLY);
                    Preconditions.checkArgument("1".equals(lastBankInterfaceSerialStatus) || "2".equals(lastBankInterfaceSerialStatus),
                            "请先提交[开卡申请]");
                }
            }
        }
    }

    /**
     * 订单状态Text
     *
     * @param loanProcessDO
     * @return
     */
    private String getOrderStatusText(LoanProcessDO loanProcessDO) {
        String orderStatusText = ORDER_STATUS_CANCEL.equals(loanProcessDO.getOrderStatus()) ? "[已弃单]" : (ORDER_STATUS_END.equals(loanProcessDO.getOrderStatus()) ? "[已结单]" : "[状态异常]");
        return orderStatusText;
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
     * @param loanProcessDO
     */
    private void execCreditSupplementTask(ApprovalParam approval, String processInstanceId, LoanProcessDO loanProcessDO) {

        // finish   ==》 open 【银行征信】/【社会征信】
        finishTask_(approval);

        approval.setOriginalTaskDefinitionKey(approval.getTaskDefinitionKey());

        // 判断当前任务流程   是否在电审前
        Preconditions.checkArgument(TASK_PROCESS_INIT.equals(loanProcessDO.getTelephoneVerify()), "流程已过[电审]，无法发起[征信增补]");

        // 当前所有task
        List<Task> tasks = getCurrentTaskList(processInstanceId);

        Preconditions.checkArgument(!CollectionUtils.isEmpty(tasks), "无可执行任务");

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

                    if (isBankAndSocialCreditRecordTask) {
                        approval.setTaskDefinitionKey(task.getTaskDefinitionKey());
                    }
                    // [贷款申请] && [上门家访]   -- 2个任务时，必须用[贷款申请]的KEY
                    else if (isLoanApplyVisitVerifyFilterTask && tasks.size() >= 2) {
                        approval.setTaskDefinitionKey(LOAN_APPLY.getCode());
                    }
                    // [贷款申请] && [上门家访]   -- 单个任务时，用当前KEY即可
                    else if (isLoanApplyVisitVerifyFilterTask) {
                        approval.setTaskDefinitionKey(task.getTaskDefinitionKey());
                    }

                });
    }

    /**
     * 执行【资料流转（抵押资料 - 合伙人->公司）】任务
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
            asyncPackZipFile(approval.getTaskDefinitionKey(), loanProcessDO, 2);

            return ResultBean.ofSuccess(null, "[资料增补]提交成功");
        }

        return ResultBean.ofError("action参数有误");
    }

    /**
     * 异步推送
     *
     * @param orderId
     * @param loanBaseInfoId
     * @param taskDefinitionKey
     * @param approval
     */
    private void asyncPush(Long orderId, Long loanBaseInfoId, String taskDefinitionKey, ApprovalParam approval) {
        if (!approval.isNeedPush()) {
            return;
        }

        executorService.execute(() -> {

            logger.info("jpush ---------- start ");

            LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanBaseInfoId);
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
            Long loanCustomerId = null;
            if (loanOrderDO != null) {
                loanCustomerId = loanOrderDO.getLoanCustomerId();
            }
            LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanCustomerId, new Byte("0"));

            if (loanBaseInfoDO != null && !LoanProcessEnum.CREDIT_APPLY.getCode().equals(taskDefinitionKey)) {
                String title = "你有一个新的消息";
                String prompt = "你提交的订单被管理员审核啦";
                String msg = "详细信息请联系管理员";

                String taskName = LoanProcessEnum.getNameByCode(taskDefinitionKey);
                //审核结果：0-REJECT / 1-PASS / 2-CANCEL / 3-资料增补
                String result = "[异常]";
                switch (approval.getAction().intValue()) {
                    case 0:
                        result = "[已打回]";
                        break;
                    case 1:
                        result = "[已通过]";
                        break;
                    case 2:
                        result = "[已弃单]";
                        break;
                    case 3:
                        result = "[发起资料增补]";
                        break;
                    default:
                        result = "[异常]";
                }
                title = taskName + result;

                if (loanCustomerDO != null) {
                    prompt = "主贷人:[" + loanCustomerDO.getName() + "]-" + title;
                }
                msg = StringUtils.isBlank(approval.getInfo()) ? "无" : "null".equals(approval.getInfo()) ? "无" : approval.getInfo();

                FlowOperationMsgDO DO = new FlowOperationMsgDO();
                DO.setEmployeeId(loanBaseInfoDO.getSalesmanId());
                DO.setOrderId(orderId);
                DO.setTitle(title);
                DO.setPrompt(prompt);
                DO.setMsg(msg);

                EmployeeDO loginUser = null;
                try {

                    loginUser = SessionUtils.getLoginUser();
                } catch (Exception ex) {
                    logger.info("自动任务 || 未登录");
                }

                if (null == loginUser) {

                    // 自动任务
                    DO.setSender("auto");

                } else {

                    String loginUserName = loginUser.getName();
                    DO.setSender(loginUserName);
                }

                DO.setProcessKey(taskDefinitionKey);
                DO.setSendDate(new Date());
                DO.setReadStatus(new Byte("0"));
                DO.setType(approval.getAction());

                // TODO test
                logger.info("jpush ---------- push" + JSON.toJSONString(DO));

                jpushService.push(DO);
            }

        });
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
            completeTask(task.getId(), variables);
        }

        // 征信申请记录拦截
        execCreditRecordFilterTask(task, loanOrderDO.getProcessInstId(), approval, variables);

        // 业务申请 & 上门调查 拦截
        execLoanApplyVisitVerifyFilterTask(task, loanOrderDO.getProcessInstId(), approval, variables, loanOrderDO, loanProcessDO);
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
     * 更新新产生的任务状态
     *
     * @param loanProcessDO
     * @param processInstanceId
     * @param startTaskIdList
     * @param approval
     * @param currentLoanProcessDO
     */
    private void updateNextTaskProcessStatus(LoanProcessDO loanProcessDO, String processInstanceId, List<String> startTaskIdList,
                                             ApprovalParam approval, LoanProcessDO currentLoanProcessDO) {

        // 获取提交之后的待执行任务列表
        List<Task> endTaskList = getCurrentTaskList(processInstanceId);

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

            // 是否已过电审
            if (!TASK_PROCESS_DONE.equals(currentLoanProcessDO.getTelephoneVerify())) {
                // 没过电审
                doUpdateNextTaskProcessStatus(oldTaskList, loanProcessDO, TASK_PROCESS_INIT, approval);
            } else {
                // 过了电审，则不是真正的全部打回      nothing
            }

            // 打回记录
            createRejectLog(newTaskList, loanProcessDO, approval.getTaskDefinitionKey(), approval.getInfo());

        } else if (ACTION_CANCEL.equals(action)) {
            // nothing
        }
    }

    private List<String> getNewTaskKeyList(String processInstanceId, List<String> startTaskIdList) {

        // 获取提交之后的待执行任务列表
        List<Task> endTaskList = getCurrentTaskList(processInstanceId);

        if (CollectionUtils.isEmpty(endTaskList)) {
            return null;
        }

        // 筛选出新产生的任务Key
        List<String> newTaskKeyList = Lists.newArrayList();

        endTaskList.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    if (!startTaskIdList.contains(e.getId())) {
                        // 不存在：新产生的任务
                        newTaskKeyList.add(e.getTaskDefinitionKey());
                    }
                });

        return newTaskKeyList;
    }

    /**
     * 打回记录
     *
     * @param newTaskList
     * @param loanProcessDO
     * @param rejectOriginTask
     * @param reason
     */
    private void createRejectLog(List<Task> newTaskList, LoanProcessDO loanProcessDO, String rejectOriginTask, String reason) {

        if (!CollectionUtils.isEmpty(newTaskList)) {

            newTaskList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        String rejectToTask = getRejectToTask(e.getTaskDefinitionKey());

                        LoanRejectLogDO loanRejectLogDO = new LoanRejectLogDO();
                        loanRejectLogDO.setOrderId(loanProcessDO.getOrderId());
                        loanRejectLogDO.setRejectOriginTask(rejectOriginTask);
                        loanRejectLogDO.setRejectToTask(rejectToTask);
                        loanRejectLogDO.setReason(reason);
                        loanRejectLogDO.setGmtCreate(new Date());

                        int count = loanRejectLogDOMapper.insertSelective(loanRejectLogDO);
                        Preconditions.checkArgument(count > 0, "打回记录失败");
                    });
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
                        updateCurrentTaskProcessStatus(loanProcessDO, task.getTaskDefinitionKey(), taskProcessStatus, approval);
                    });
        }
    }


    /**
     * 更新本地已执行的任务状态
     *
     * @param loanProcessDO
     * @param taskDefinitionKey
     * @param taskProcessStatus
     * @param approval
     */
    private void updateCurrentTaskProcessStatus(LoanProcessDO loanProcessDO, String taskDefinitionKey,
                                                Byte taskProcessStatus, ApprovalParam approval) {

        if (null == taskProcessStatus) {
            return;
        }

        if (taskDefinitionKey.startsWith("filter")) {
            return;
        }

        // 更新资料流转type
        doUpdateDataFlowType(loanProcessDO, taskDefinitionKey, taskProcessStatus, approval);

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
    private void doUpdateCurrentTaskProcessStatus(LoanProcessDO loanProcessDO, String taskDefinitionKey, Byte taskProcessStatus) {
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
            Class<? extends LoanProcessDO> loanProcessDOClass = loanProcessDO.getClass();
            // 获取对应method
            Method method = loanProcessDOClass.getMethod(methodName, Byte.class);
            // 执行method
            Object result = method.invoke(loanProcessDO, taskProcessStatus);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 更新资料流转type
     *
     * @param loanProcessDO
     * @param taskDefinitionKey
     * @param taskProcessStatus
     * @param approval
     */
    private void doUpdateDataFlowType(LoanProcessDO loanProcessDO, String taskDefinitionKey, Byte taskProcessStatus, ApprovalParam approval) {

        // 如果是：[资料流转]节点
        if (taskDefinitionKey.startsWith(DATA_FLOW_TASK_KEY_PREFIX)) {

            String[] taskKeyArr = taskDefinitionKey.split(DATA_FLOW_TASK_KEY_REVIEW_SUFFIX);

            // 是否为：[确认接收]节点     _review
            boolean is_review_task_key = taskDefinitionKey.endsWith(DATA_FLOW_TASK_KEY_REVIEW_SUFFIX) && taskKeyArr.length == 1;

            // send   task-key
            String send_task_key = taskKeyArr[0];
            // review task-key
            String review_task_key = null;

            if (is_review_task_key) {
                review_task_key = taskDefinitionKey;
            } else {
                review_task_key = taskDefinitionKey + DATA_FLOW_TASK_KEY_REVIEW_SUFFIX;
            }

            // taskKey - type  映射
            Map<String, String> codeKMap = dictService.getCodeKMap("loanDataFlowType");

            // send-type
            Byte send_type = Byte.valueOf(codeKMap.get(send_task_key));
            // review-type
            Byte review_type = Byte.valueOf(codeKMap.get(review_task_key));

            Preconditions.checkNotNull(send_type, "资料流转-taskDefinitionKey异常");
            Preconditions.checkNotNull(review_type, "资料流转-taskDefinitionKey异常");


            // send   存在状态：1、2、3
            if (!is_review_task_key) {

                // 1
                if (TASK_PROCESS_DONE.equals(taskProcessStatus)) {

                    // update  type -> next_review_taskKey--type

                    updateDataFlowType(loanProcessDO.getOrderId(), send_type, review_type);
                }

                // 2   新建记录
                else if (TASK_PROCESS_TODO.equals(taskProcessStatus)) {

                    // create  type -> send_taskKey--type

                    preRecordDataFlowOrderAndType(loanProcessDO.getOrderId(), send_type, approval);
                }

                // 3
                else if (TASK_PROCESS_REJECT.equals(taskProcessStatus)) {

                    // update   type -> send_taskKey--type

                    updateDataFlowType(loanProcessDO.getOrderId(), review_type, send_type);
                }

            }

            // _review    存在状态：0、1、2
            else {

                // 0
                if (TASK_PROCESS_INIT.equals(taskProcessStatus)) {

                    // update   type -> send_taskKey--type

                    updateDataFlowType(loanProcessDO.getOrderId(), review_type, send_type);
                }

                // 1
                else if (TASK_PROCESS_DONE.equals(taskProcessStatus)) {

                    // nothing    让下一个节点自己create

                }

                // 2
                else if (TASK_PROCESS_TODO.equals(taskProcessStatus)) {

                    // update  type -> next_review_taskKey--type

                    updateDataFlowType(loanProcessDO.getOrderId(), send_type, review_type);
                }

            }
        }
    }

    /**
     * 更新资料流转type
     *
     * @param orderId
     * @param current_type
     * @param to_be_update_type
     */
    private void updateDataFlowType(Long orderId, Byte current_type, Byte to_be_update_type) {

        LoanDataFlowDO loanDataFlowDO = loanDataFlowService.getLastByOrderIdAndType(orderId, current_type);

        if (null != loanDataFlowDO) {

            loanDataFlowDO.setType(to_be_update_type);

            ResultBean updateResultBean = loanDataFlowService.update(loanDataFlowDO);
            Preconditions.checkArgument(updateResultBean.getSuccess(), updateResultBean.getMsg());
        }
    }


    /**
     * 资料流转-SEND-TODO待办节点-预处理：插入一条待处理的节点记录 -> orderId、type
     *
     * @param orderId
     * @param sendType
     * @param approval
     */
    private void preRecordDataFlowOrderAndType(Long orderId, Byte sendType, ApprovalParam approval) {

        // 【资料流转（抵押资料 - 合伙人->公司）】任务 -[新建]
        if (isDataFlowMortgageP2cNewTask(approval.getOriginalTaskDefinitionKey(), approval.getOriginalAction())) {
            // [提交之前]已经create过了  不能重复创建
            return;
        }

        LoanDataFlowDO loanDataFlowDO = new LoanDataFlowDO();

        loanDataFlowDO.setOrderId(orderId);
        loanDataFlowDO.setType(sendType);

        ResultBean result = loanDataFlowService.create(loanDataFlowDO);
        Preconditions.checkArgument(result.getSuccess(), result.getMsg());
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
     * @param orderId
     * @return
     */
    private LoanProcessDO getLoanProcess(Long orderId) {
        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

        return loanProcessDO;
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
            completeTask(task.getId(), variables);
        } else if (ACTION_REJECT_MANUAL.equals(approval.getAction())) {
            // 手动打回
            variables.put(PROCESS_VARIABLE_TARGET, LOAN_APPLY.getCode());
            completeTask(task.getId(), variables);
        } else if (ACTION_REJECT_AUTO.equals(approval.getAction())) {
            // 自动打回
            variables.put(PROCESS_VARIABLE_TARGET, CREDIT_APPLY.getCode());
            completeTask(task.getId(), variables);
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
        completeTask(task.getId(), variables);
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
     * 自动完成指定任务
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

        completeTask(task.getId(), variables);

        // 更新流程记录
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(orderId);
        updateCurrentTaskProcessStatus(loanProcessDO, taskDefinitionKey, TASK_PROCESS_DONE, approval);
        updateLoanProcess(loanProcessDO);
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

        ResultBean<Void> approvalResult = approval(approvalParam);
        Preconditions.checkArgument(approvalResult.getSuccess(), approvalResult.getMsg());
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
        boolean isSocialTask_TargetIs_LoanApplyVisitVerifyFilterTask =
                SOCIAL_CREDIT_RECORD.getCode().equals(approval.getTaskDefinitionKey())
                        && ACTION_PASS.equals(approval.getAction())
                        && LOAN_APPLY_VISIT_VERIFY_FILTER.getCode().equals(target);

        // 执行拦截任务
        if (loanApplyVisitVerifyFilterTask || isSocialTask_TargetIs_LoanApplyVisitVerifyFilterTask) {

            // 获取所有正在执行的并行任务
            List<Task> tasks = getCurrentTaskList(processInstId);

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
        Task visitVerifyTask = getTask(loanOrderDO.getProcessInstId(), VISIT_VERIFY.getCode());

        // PASS
        if (null != visitVerifyTask) {
            completeTask(visitVerifyTask.getId(), passVariables);
        }


        // 获取当前所有正在执行的任务
        List<Task> currentTaskList = getCurrentTaskList(loanOrderDO.getProcessInstId());

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
        updateLoanProcess(loanProcessDO);

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
                                completeTask(task.getId(), passVariables);
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
            List<Task> tasks = getCurrentTaskList(processInstId);

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
        updateLoanProcess(loanProcessDO);

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

        List<Task> runTaskList = Lists.newArrayList();
        runTaskList.addAll(insteadPayTaskList);
        runTaskList.addAll(collectionTaskList);
        runTaskList.addAll(loanProcessTaskList);

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

        // TODO 还要加上资料增补任务 ？？？

        return ResultBean.ofSuccess(taskStateVOS, "查询当前流程任务节点信息成功");
    }

    @Override
    public ResultBean<TaskStateVO> taskStatus(Long orderId, String taskDefinitionKey) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(taskDefinitionKey), "任务Key不能为空");

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

            } else {
                taskStatus = getTaskStatus(loanProcessDO, taskDefinitionKey);
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
                                + getRejectInfo(e.getAction(), e.getInfo());

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
     * 打回理由
     *
     * @param action
     * @param info
     * @return
     */
    private String getRejectInfo(Byte action, String info) {

        if (ACTION_REJECT_MANUAL.equals(action)) {
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
     * @param loanOrderDO
     */
    private void fillLoanAmount(Map<String, Object> variables, Byte action, String taskDefinitionKey, LoanOrderDO loanOrderDO) {

        // [征信申请] & [PASS]
        boolean isApplyVerifyTaskAndActionIsPass = CREDIT_APPLY.getCode().equals(taskDefinitionKey) && ACTION_PASS.equals(action);
        // [银行&社会征信录入]
        boolean isBankAndSocialCreditRecordTask = BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey) || SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey);
        if (isApplyVerifyTaskAndActionIsPass || isBankAndSocialCreditRecordTask) {

            // 预计贷款金额
            LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());

            // 流程变量
            variables.put(PROCESS_VARIABLE_LOAN_AMOUNT_EXPECT, loanBaseInfoDO.getLoanAmount());
        }

        // [贷款申请]
        if (LOAN_APPLY.getCode().equals(taskDefinitionKey)) {

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
            variables.put(PROCESS_VARIABLE_LOAN_AMOUNT_EXPECT, expectLoanAmount);
            variables.put(PROCESS_VARIABLE_LOAN_AMOUNT_ACTUAL, actualLoanAmount);

            // 预计 < 13W,但实际 >= 13W
            if (EXPECT_LOAN_AMOUNT_LT_13W.equals(expectLoanAmount) && null != actualLoanAmount && actualLoanAmount >= ACTUAL_LOAN_AMOUNT_13W) {

                List<LoanCreditInfoDO> customerIdAndType = loanCreditInfoDOMapper.getByCustomerIdAndType(loanOrderDO.getLoanCustomerId(), CREDIT_TYPE_SOCIAL);
                // 没录过[社会征信]
                if (CollectionUtils.isEmpty(customerIdAndType)) {
                    // target
                    variables.put(PROCESS_VARIABLE_TARGET, SOCIAL_CREDIT_RECORD.getCode());
                }
            }
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
//        variables.put(PROCESS_VARIABLE_TARGET, approval.getTarget());

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

        // 【业务申请】
        if (LOAN_APPLY.getCode().equals(approval.getTaskDefinitionKey())) {

            // 是否 [打回] - 自于【资料审核】

            // 如果为【打回】
            if (TASK_PROCESS_REJECT.equals(loanProcessDO.getLoanApply())) {

                // 2.是否打回自【资料审核】
                LoanRejectLogDO loanRejectLogDO = loanRejectLogDOMapper.lastByOrderIdAndTaskDefinitionKey(approval.getOrderId(), approval.getTaskDefinitionKey());

                if (null != loanRejectLogDO) {
                    // 是
                    if (MATERIAL_REVIEW.getCode().equals(loanRejectLogDO.getRejectOriginTask())) {
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
            } else {
                // 否
                variables.put(PROCESS_VARIABLE_REJECT_ORIGIN_TASK, null);
            }

        }

        // 【电审】
        else if (TELEPHONE_VERIFY.getCode().equals(approval.getTaskDefinitionKey())) {

            // PASS
            if (ACTION_PASS.equals(approval.getAction())) {
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
            else if (ACTION_REJECT_MANUAL.equals(approval.getAction())) {
                variables.put(PROCESS_VARIABLE_TARGET, LOAN_APPLY.getCode());
            }
        }

        // [社会征信] & [PASS] &   [loan_apply 补充 [社会征信]]
        else if (SOCIAL_CREDIT_RECORD.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {

            if (null != variables.get(PROCESS_VARIABLE_TARGET)) {
                return;
            }

            if (TASK_PROCESS_DONE.equals(loanProcessDO.getLoanApply())) {
                variables.put(PROCESS_VARIABLE_TARGET, LOAN_APPLY_VISIT_VERIFY_FILTER.getCode());
            }

        }

        // [合同套打]
        else if (MATERIAL_PRINT_REVIEW.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {

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

        // [资料流转确认（合同资料 - 公司->银行）]
        else if (DATA_FLOW_CONTRACT_C2B_REVIEW.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {

            // [资料流转（抵押资料 - 合伙人->公司）]是否已存在
            Byte dataFlowMortgageP2cStatus = loanProcessDO.getDataFlowMortgageP2c();
            // 是否走了另一条线    -> 新建
            boolean otherFlow = TASK_PROCESS_TODO.equals(dataFlowMortgageP2cStatus)
                    || TASK_PROCESS_DONE.equals(dataFlowMortgageP2cStatus)
                    || TASK_PROCESS_REJECT.equals(dataFlowMortgageP2cStatus);

            // 是否走了另一条线
            if (otherFlow) {
                variables.put(PROCESS_VARIABLE_TARGET, BANK_LEND_RECORD.getCode());
            } else {
                // nothing
                variables.put(PROCESS_VARIABLE_TARGET, StringUtils.EMPTY);
            }
        }

        // [资料流转确认（抵押资料 - 公司->银行）]
        else if (DATA_FLOW_MORTGAGE_C2B_REVIEW.getCode().equals(approval.getTaskDefinitionKey()) && ACTION_PASS.equals(approval.getAction())) {
            // 是否从前面的流程节点走过来的：  No.1 -> [资料流转（抵押资料 - 银行->公司）]
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
                && !MATERIAL_REVIEW.getCode().equals(variables.get(PROCESS_VARIABLE_REJECT_ORIGIN_TASK));
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

            long count = tasks.stream()
                    .filter(Objects::nonNull)
                    .filter(e -> BANK_CREDIT_RECORD.getCode().equals(e.getTaskDefinitionKey())
                            || SOCIAL_CREDIT_RECORD.getCode().equals(e.getTaskDefinitionKey()))
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
                                completeTask(task.getId(), passVariables);

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
                                cancelVariables.clear();
                            }

                        });
            }

            // "主任务"  ->  打回
            Task currentFilterTask = onlyAsMainTask[0];
            Map<String, Object> rejectVariables = Maps.newHashMap();
            rejectVariables.put(PROCESS_VARIABLE_ACTION, ACTION_REJECT_MANUAL);
            completeTask(currentFilterTask.getId(), rejectVariables);
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
     * 并行任务 -弃单
     *
     * @param processInstanceId
     */
    private void dealCancelTask(String processInstanceId) {
        // 弃单 -> 直接终止流程
        runtimeService.deleteProcessInstance(processInstanceId, "弃单");
    }

    /**
     * 获取当前待执行任务列表
     *
     * @param processInstanceId
     * @return
     */
    public List<Task> getCurrentTaskList(String processInstanceId) {
        List<Task> currentTaskList = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        return currentTaskList;
    }

    /**
     * 获取当前待执行任务ID列表
     *
     * @param processInstanceId
     * @return
     */
    public List<String> getCurrentTaskIdList(String processInstanceId) {

        List<Task> currentTaskList = getCurrentTaskList(processInstanceId);

        if (!CollectionUtils.isEmpty(currentTaskList)) {

            List<String> currentTaskIdList = currentTaskList.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {
                        return e.getId();
                    })
                    .collect(toList());

            return currentTaskIdList;
        }

        return null;
    }
}
