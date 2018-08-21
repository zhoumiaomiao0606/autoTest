package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.JpushService;
import com.yunche.loan.service.LoanProcessApprovalCommonService;
import com.yunche.loan.service.TaskDistributionService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.yunche.loan.config.constant.LoanProcessConst.LOAN_PROCESS_COLLECTION_KEYS;
import static com.yunche.loan.config.constant.LoanProcessConst.LOAN_PROCESS_INSTEAD_PAY_KEYS;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_PASS;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_REJECT_AUTO;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_REJECT_MANUAL;
import static com.yunche.loan.config.constant.LoanProcessEnum.BANK_SOCIAL_CREDIT_RECORD_FILTER;
import static com.yunche.loan.config.constant.LoanProcessEnum.CREDIT_APPLY;
import static com.yunche.loan.config.thread.ThreadPool.executorService;
import static java.util.stream.Collectors.toList;

/**
 * @author liuzhe
 * @date 2018/8/21
 */
@Service
public class LoanProcessApprovalCommonServiceImpl implements LoanProcessApprovalCommonService {

    private static final Logger logger = LoggerFactory.getLogger(LoanProcessApprovalCommonServiceImpl.class);


    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanProcessLogDOMapper loanProcessLogDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanRejectLogDOMapper loanRejectLogDOMapper;

    @Autowired
    private LoanProcessDOMapper loanProcessDOMapper;

    @Autowired
    private LoanProcessInsteadPayDOMapper loanProcessInsteadPayDOMapper;

    @Autowired
    private LoanProcessCollectionDOMapper loanProcessCollectionDOMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDistributionService taskDistributionService;

    @Autowired
    private JpushService jpushService;


    /**
     * 流程操作日志记录
     *
     * @param approval
     */
    @Override
    public void log(ApprovalParam approval) {
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
     * 获取当前执行任务（activiti中）
     *
     * @param processInstId
     * @param taskDefinitionKey
     * @return
     */
    @Override
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
     * 获取当前待执行任务列表
     *
     * @param processInstanceId
     * @return
     */
    @Override
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
    @Override
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

    /**
     * [领取]完成
     *
     * @param approval
     * @param startTaskIdList
     * @param processInstId
     */
    @Override
    public void finishTask(ApprovalParam approval, List<String> startTaskIdList, String processInstId) {

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
     * 异步推送
     *
     * @param loanOrderDO
     * @param approval
     */
    @Override
    public void asyncPush(LoanOrderDO loanOrderDO, ApprovalParam approval) {
        if (!approval.isNeedPush()) {
            return;
        }

        executorService.execute(() -> {

            LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());
            Long loanCustomerId = null;
            if (loanOrderDO != null) {
                loanCustomerId = loanOrderDO.getLoanCustomerId();
            }
            LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanCustomerId, new Byte("0"));

            if (loanBaseInfoDO != null && !LoanProcessEnum.CREDIT_APPLY.getCode().equals(approval.getTaskDefinitionKey())) {
                String title = "你有一个新的消息";
                String prompt = "你提交的订单被管理员审核啦";
                String msg = "详细信息请联系管理员";

                String taskName = LoanProcessEnum.getNameByCode(approval.getTaskDefinitionKey());
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
                DO.setOrderId(loanOrderDO.getId());
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

                DO.setProcessKey(approval.getTaskDefinitionKey());
                DO.setSendDate(new Date());
                DO.setReadStatus(new Byte("0"));
                DO.setType(approval.getAction());

                jpushService.push(DO);
            }

        });
    }

    /**
     * 打回记录
     *
     * @param newTaskList
     * @param orderId
     * @param rejectOriginTask
     * @param reason
     */
    @Override
    public void createRejectLog(List<Task> newTaskList, Long orderId, String rejectOriginTask, String reason) {

        if (!CollectionUtils.isEmpty(newTaskList)) {

            newTaskList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        String rejectToTask = getRejectToTask(e.getTaskDefinitionKey());

                        LoanRejectLogDO loanRejectLogDO = new LoanRejectLogDO();
                        loanRejectLogDO.setOrderId(orderId);
                        loanRejectLogDO.setRejectOriginTask(rejectOriginTask);
                        loanRejectLogDO.setRejectToTask(rejectToTask);
                        loanRejectLogDO.setReason(reason);
                        loanRejectLogDO.setGmtCreate(new Date());

                        int count = loanRejectLogDOMapper.insertSelective(loanRejectLogDO);
                        Preconditions.checkArgument(count > 0, "打回记录失败");
                    });
        }
    }

    @Override
    public LoanProcessDO_ getLoanProcess(Long orderId, Long processId, String taskDefinitionKey) {
        Preconditions.checkNotNull(orderId, "orderId不能为空");
        Preconditions.checkNotNull(taskDefinitionKey, "taskDefinitionKey不能为空");

        LoanProcessDO_ loanProcessDO_ = null;

        // 1 -> 1
        if (null == processId) {

            boolean isNotInsteadPayAndCollectionTask = !LOAN_PROCESS_INSTEAD_PAY_KEYS.contains(taskDefinitionKey)
                    && !LOAN_PROCESS_COLLECTION_KEYS.contains(taskDefinitionKey);

            if (isNotInsteadPayAndCollectionTask) {

                loanProcessDO_ = loanProcessDOMapper.selectByPrimaryKey(orderId);

            } else {

                Preconditions.checkNotNull(processId, "processId不能为空");
            }
        }

        // 1 -> 多
        else {

            // 代偿流程
            if (LOAN_PROCESS_INSTEAD_PAY_KEYS.contains(taskDefinitionKey)) {

                loanProcessDO_ = loanProcessInsteadPayDOMapper.selectByPrimaryKey(processId);
            }
            // 催收流程
            else if (LOAN_PROCESS_COLLECTION_KEYS.contains(taskDefinitionKey)) {

                loanProcessDO_ = loanProcessCollectionDOMapper.selectByPrimaryKey(processId);
            } else {

                throw new BizException("taskDefinitionKey有误");
            }
        }

        Preconditions.checkNotNull(loanProcessDO_, "流程记录丢失");

        return loanProcessDO_;
    }

    /**
     * 获取业务单
     *
     * @param orderId
     * @return
     */
    @Override
    public LoanOrderDO getLoanOrder(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");
        Preconditions.checkNotNull(loanOrderDO.getProcessInstId(), "流程实例ID不存在");

        return loanOrderDO;
    }

    private String getRejectToTask(String taskDefinitionKey) {
        if (BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode().equals(taskDefinitionKey)) {
            return CREDIT_APPLY.getCode();
        }
        return taskDefinitionKey;
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

}
