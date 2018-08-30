package com.yunche.loan.service;

import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.entity.LoanProcessDO_;
import com.yunche.loan.domain.param.ApprovalParam;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * 流程审核 公用方法
 *
 * @author liuzhe
 * @date 2018/8/21
 */
public interface LoanProcessApprovalCommonService {

    /**
     * 审核日志记录
     *
     * @param approval
     */
    void log(ApprovalParam approval);

    /**
     * 获取当前执行任务（activiti中）
     *
     * @param processInstId
     * @param taskDefinitionKey
     * @return
     */
    Task getTask(String processInstId, String taskDefinitionKey);

    /**
     * 获取当前待执行任务列表
     *
     * @param processInstanceId
     * @return
     */
    List<Task> getCurrentTaskList(String processInstanceId);

    /**
     * 获取当前待执行任务ID列表
     *
     * @param processInstanceId
     * @return
     */
    List<String> getCurrentTaskIdList(String processInstanceId);

    /**
     * [领取]完成
     *
     * @param approval
     * @param startTaskIdList
     * @param processInstId
     */
    void finishTask(ApprovalParam approval, List<String> startTaskIdList, String processInstId);

    /**
     * 异步推送
     *
     * @param loanOrderDO
     * @param approval
     */
    void asyncPush(LoanOrderDO loanOrderDO, ApprovalParam approval);

    /**
     * 打回记录
     *
     * @param newTaskList
     * @param orderId
     * @param taskDefinitionKey
     * @param info
     */
    void createRejectLog(List<Task> newTaskList, Long orderId, String taskDefinitionKey, String info);

    /**
     * 获取主订单
     *
     * @param orderId
     * @return
     */
    LoanOrderDO getLoanOrder(Long orderId);

    /**
     * 获取流程记录 -消费贷
     *
     * @param orderId
     * @return
     */
    LoanProcessDO getLoanProcess(Long orderId);

    /**
     * 获取流程记录      -消费贷/上门催收/法务处理/代偿
     *
     * @param orderId
     * @param processId
     * @param taskDefinitionKey
     * @return
     */
    LoanProcessDO_ getLoanProcess_(Long orderId, Long processId, String taskDefinitionKey);

    /**
     * 执行流程数据更新   -消费贷/上门催收/法务处理/代偿
     *
     * @param loanProcessDO_
     * @param taskDefinitionKey
     * @param taskProcessStatus
     */
    void doUpdateCurrentTaskProcessStatus(LoanProcessDO_ loanProcessDO_, String taskDefinitionKey, Byte taskProcessStatus);

    /**
     * 订单状态校验
     *
     * @param orderId
     */
    void checkOrderStatus(Long orderId);

    /**
     * 更新本地流程记录
     *
     * @param loanProcessDO
     */
    void updateLoanProcess(LoanProcessDO loanProcessDO);

    /**
     * 完成任务   ==>   在activiti中完成
     *
     * @param taskId
     * @param variables
     */
    void completeTask(String taskId, Map<String, Object> variables);

    /**
     * 更新本地已执行的任务状态
     *
     * @param loanProcessDO
     * @param taskDefinitionKey
     * @param taskProcessStatus
     * @param approval
     */
    void updateCurrentTaskProcessStatus(LoanProcessDO loanProcessDO, String taskDefinitionKey,
                                        Byte taskProcessStatus, ApprovalParam approval);
}
