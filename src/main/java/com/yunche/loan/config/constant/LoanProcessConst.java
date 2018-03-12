package com.yunche.loan.config.constant;

/**
 * 消费贷流程常量
 *
 * @author liuzhe
 * @date 2018/2/27
 */
public class LoanProcessConst {
    /**
     * 打回
     */
    public static final Byte REJECT = 0;
    /**
     * 通过
     */
    public static final Byte PASS = 1;
    /**
     * 弃单
     */
    public static final Byte CANCEL = 2;
    /**
     * 资料增补
     */
    public static final Integer INFO_SUPPLEMENT = 3;

    /**
     * 任务状态:  0-全部;
     */
    public static final Byte TASK_ALL = 0;
    /**
     * 任务状态:  1-已处理(审核);
     */
    public static final Byte TASK_DONE = 1;
    /**
     * 任务状态:  2-未处理(待审核);
     */
    public static final Byte TASK_TODO = 2;
    /**
     * 任务状态：还未执行到当前任务节点
     */
    public static final Byte TASK_NOT_REACH_CURRENT = 3;

    /**
     * 删除理由  【task被删除的理由-任务已被执行】
     */
    public static final String DELETE_RELEASE_HAS_DONE = "completed";
}
