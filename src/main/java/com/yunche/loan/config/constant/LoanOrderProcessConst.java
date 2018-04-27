package com.yunche.loan.config.constant;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/3/23
 */
public class LoanOrderProcessConst {
    /**
     * 任务状态:  默认值0-未执行到此节点;
     */
    public static final Byte TASK_PROCESS_INIT = 0;
    /**
     * 任务状态:  1-已处理(审核);
     */
    public static final Byte TASK_PROCESS_DONE = 1;
    /**
     * 任务状态:  2-未处理(待审核);
     */
    public static final Byte TASK_PROCESS_TODO = 2;
    /**
     * 任务状态:  3-打回修改(待审核);
     */
    public static final Byte TASK_PROCESS_REJECT = 3;

    /**
     * 已退款
     */
    public static final Byte TASK_PROCESS_REFUND = 21;

    /**
     * 任务状态:  2-已结清;
     */
    public static final Byte TASK_PROCESS_CLOSED = 11;
    /**
     * 任务状态:  11-弃单;
     */
    public static final Byte TASK_PROCESS_CANCEL = 12;

    /**
     * 电审任务状态：电审专员已审核
     */
    public static final Byte TASK_PROCESS_TELEPHONE_VERIFY_COMMISSIONER = 4;
    /**
     * 电审任务状态：电审主管已审核
     */
    public static final Byte TASK_PROCESS_TELEPHONE_VERIFY_LEADER = 5;
    /**
     * 电审任务状态：电审经理已审核
     */
    public static final Byte TASK_PROCESS_TELEPHONE_VERIFY_MANAGER = 6;
    /**
     * 电审任务状态：总监已审核
     */
    public static final Byte TASK_PROCESS_TELEPHONE_VERIFY_DIRECTOR = 7;


    /**
     * 订单状态：进行中
     */
    public static final Byte ORDER_STATUS_DOING = 1;
    /**
     * 订单状态：已完结
     */
    public static final Byte ORDER_STATUS_END = 2;
    /**
     * 订单状态：已弃单
     */
    public static final Byte ORDER_STATUS_CANCEL = 3;


    public static final Map<String, String> taskDefinitionKeyFiledMap = Maps.newHashMap();

    static {
//        taskDefinitionKeyFiledMap.put(CREDIT_APPLY.getCode(), );
    }
}
