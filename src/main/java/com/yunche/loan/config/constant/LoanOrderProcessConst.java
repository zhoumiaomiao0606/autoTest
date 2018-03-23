package com.yunche.loan.config.constant;

/**
 * @author liuzhe
 * @date 2018/3/23
 */
public class LoanOrderProcessConst {
    /**
     * 任务状态:  1-已处理(审核);
     */
    public static final Byte TASK_PROCESS_DONE = 1;
    /**
     * 任务状态:  2-未处理(待审核);
     */
    public static final Byte TASK_PROCESS_TODO = 2;

    public static final Byte TASK_PROCESS_BE_REJECT = 3;

    public static final Byte TASK_PROCESS_TELEPHONE_VERIFY_COMMISSIONER = 4;
    public static final Byte TASK_PROCESS_TELEPHONE_VERIFY_LEADER = 5;
    public static final Byte TASK_PROCESS_TELEPHONE_VERIFY_MANAGER = 6;
    public static final Byte TASK_PROCESS_TELEPHONE_VERIFY_DIRECTOR = 7;
}
