package com.yunche.loan.config.constant;

/**
 * @author liuzhe
 * @date 2018/7/13
 */
public class ListQueryTaskStatusConst {

    /**
     * 0-全部;
     */
    public static final Integer TASK_STATUS_0 = 0;

    /**
     * 1-已提交;
     */
    public static final Integer TASK_STATUS_1_DONE = 1;

    /**
     * 2-未提交;
     */
    public static final Integer TASK_STATUS_2_TODO = 2;

    /**
     * 3-已打回;
     */
    public static final Integer TASK_STATUS_3_REJECT = 3;

    /**
     * ------------------------------------------------
     * 21-待邮寄;
     * ------------------------------------------------
     * [2-未提交 ===拆分为===>（21-待邮寄;  22-待接收;）]
     * ------------------------------------------------
     */
    public static final Integer TASK_STATUS_21_OF_DATA_FLOW_TO_BE_SEND = 21;

    /**
     * ------------------------------------------------
     * 22-待接收;
     * ------------------------------------------------
     * [2-未提交 ===拆分为===>（21-待邮寄;  22-待接收;）]
     * ------------------------------------------------
     */
    public static final Integer TASK_STATUS_22_OF_DATA_FLOW_TO_BE_RECEIVED = 22;
}
