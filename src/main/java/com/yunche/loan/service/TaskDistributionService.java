package com.yunche.loan.service;

import com.yunche.loan.domain.vo.TaskDisVO;

import java.util.List;

public interface TaskDistributionService {

    /**
     * 领取-task            ==> insert     [status - 2]
     *
     * @param taskId
     * @param taskkey
     */
    void get(Long taskId, String taskkey, Long orderId);

    /**
     * 取消领取-task         ==> del
     *  @param taskId
     * @param taskKey
     * @param orderId
     */
    void release(Long taskId, String taskKey, Long orderId);

    /**
     * 查询当前任务-领取详情
     *
     * @param taskId
     * @param taskKey
     * @return
     */
    TaskDisVO query(Long taskId, String taskKey);

    /**
     * finish-tasks         ==> status - 1
     *
     * @param taskId
     * @param orderId
     * @param taskKey
     */
    void finish(Long taskId, Long orderId, String taskKey);

    /**
     * open-tasks           ==> status - 2
     *
     * @param taskId
     * @param orderId
     * @param taskKeys
     */
    void rejectFinish(Long taskId, Long orderId, List<String> taskKeys);
}
