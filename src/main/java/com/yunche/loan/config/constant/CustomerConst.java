package com.yunche.loan.config.constant;

/**
 * @author liuzhe
 * @date 2018/2/26
 */
public class CustomerConst {
    /**
     * 客户类型：主贷人-1
     */
    public static final Byte CUST_TYPE_PRINCIPAL = 1;
    /**
     * 客户类型：共贷人-2
     */
    public static final Byte CUST_TYPE_COMMON = 2;
    /**
     * 客户类型：担保人-3
     */
    public static final Byte CUST_TYPE_GUARANTOR = 3;
    /**
     * 客户类型：紧急联系人-4
     */
    public static final Byte CUST_TYPE_EMERGENCY_CONTACT = 4;

    /**
     * 银行征信
     */
    public static final Byte CREDIT_TYPE_BANK = 1;
    /**
     *
     */
    public static final Byte CREDIT_TYPE_SOCIAL = 2;
}
