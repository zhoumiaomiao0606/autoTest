package com.yunche.loan.config.constant;

/**
 * @author liuzhe
 * @date 2018/4/26
 */
public class ApplyOrderStatusConst {

    /**
     * 初始状态
     */
    public static final Byte APPLY_ORDER_INIT = 0;
    /**
     * 已通过
     */
    public static final Byte APPLY_ORDER_PASS = 1;
    /**
     * 待审核
     */
    public static final Byte APPLY_ORDER_TODO = 2;
    /**
     * 已打回
     */
    public static final Byte APPLY_ORDER_REJECT = 3;

    /**
     * 弃单
     */
    public static final Byte APPLY_ORDER_CANCEL = 12;
}
