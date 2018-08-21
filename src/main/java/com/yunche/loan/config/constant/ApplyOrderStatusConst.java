package com.yunche.loan.config.constant;

/**
 * [金融方案申请单] & [退款申请单]
 *
 * @author liuzhe
 * @date 2018/4/26
 */
public class ApplyOrderStatusConst {


    /////////////////////////////////  申请单 <--> 申请单-确认    ==> [无暂存] ////////////////////////////////////////////
    /**
     * 初始状态
     */
    public static final Byte APPLY_ORDER_INIT = 0;
    /**
     * 申请单-确认  -已提交
     */
    public static final Byte APPLY_ORDER_PASS = 1;
    /**
     * 申请单       -已提交
     * 申请单-确认  -待处理
     */
    public static final Byte APPLY_ORDER_TODO = 2;
    /**
     * 申请单 -已打回
     */
    public static final Byte APPLY_ORDER_REJECT = 3;
    /**
     * 弃单
     */
    public static final Byte APPLY_ORDER_CANCEL = 12;
    /////////////////////////////////  申请单 <--> 申请单-确认    ==> [无暂存] ////////////////////////////////////////////


    /**
     * 申请单 <--> 申请单-确认    ==> [有暂存]
     */
    public static class xxx {
        /**
         * 初始状态
         */
        public static final Byte APPLY_ORDER_INIT = 0;
        /**
         * 申请单-确认  -已提交
         */
        public static final Byte APPLY_ORDER_PASS = 1;
        /**
         * 申请单       -待处理（暂存）
         */
        public static final Byte APPLY_ORDER_TODO = 2;
        /**
         * 申请单      -已打回
         */
        public static final Byte APPLY_ORDER_REJECT = 3;
        /**
         * 申请单       -已提交
         * 申请单-确认  -待处理
         */
        public static final Byte APPLY_ORDER_REVIEW_TODO = 4;
    }
}
