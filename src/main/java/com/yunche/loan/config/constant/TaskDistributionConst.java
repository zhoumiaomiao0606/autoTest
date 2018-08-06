package com.yunche.loan.config.constant;

/**
 * 任务分配
 *
 * @author liuzhe
 * @date 2018/8/6
 */
public class TaskDistributionConst {

    /**
     * 该任务已被打回
     */
    public static final Byte TASK_STATUS_REJECT = 0;

    /**
     * 该任务已被完成
     */
    public static final Byte TASK_STATUS_DONE = 1;


    /**
     * 该任务已被领取,正在执行中
     */
    public static final Byte TASK_STATUS_DOING = 2;
}
