package com.yunche.loan.config.constant;

/**
 * @author liuzhe
 * @date 2018/1/22
 */
public class BaseConst {

    // 1
    public static final Byte ONE = 1;
    // 2
    public static final Byte TWO = 2;
    // 3
    public static final Byte THREE = 3;
    /**
     * 有效
     */
    public static final Byte VALID_STATUS = 0;
    /**
     * 无效
     */
    public static final Byte INVALID_STATUS = 1;

    public static final Byte UNCOLLECTEDKEY = 2;

    public static final Byte COLLECTEDKEY = 1;

    /**
     * 有效
     */
    public static final Byte WHITE_CLONE = 0;
    /**
     * 无效
     */
    public static final Byte WHITE_OPEN = 1;
    /**
     * 已删除
     */
    public static final Byte DEL_STATUS = 2;


    /**
     * 处理中
     */
    public static final Byte DOING_STATUS = 3;


    /**
     * 是
     */
    public static final Byte K_YORN_YES = 1;
    /**
     * 否
     */
    public static final Byte K_YORN_NO = 0;

//   IDict.K_DKZT
//    /**
//     * 有效
//     */
//    public static final Byte REMIT_STATUS_ZERO = 0;
//    /**
//     * 无效
//     */
//    public static final Byte REMIT_STATUS_ONE = 1;
//    /**
//     * 已删除
//     */
//    public static final Byte REMIT_STATUS_TWO = 2;
//
//    public static final Byte REMIT_STATUS_THREE = 3;

    public static final String OSS_PREFIX = "http://yunche-2018.oss-cn-hangzhou.aliyuncs.com";

    // 主贷人
    public static final Byte MASTER_CUST = 1;
    // 共还人
    public static final Byte COMM_CUST = 2;
    // 担保人
    public static final Byte ASSURE_CUST = 3;
}
