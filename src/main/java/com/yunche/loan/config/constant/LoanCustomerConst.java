package com.yunche.loan.config.constant;

/**
 * @author liuzhe
 * @date 2018/2/26
 */
public class LoanCustomerConst {

    ////////////////////////////////////////////////// 客户类型 /////////////////////////////////////////////////////////
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
    ////////////////////////////////////////////////// 客户类型 /////////////////////////////////////////////////////////


    ////////////////////////////////////////////////// 担保类型 /////////////////////////////////////////////////////////
    /**
     * 担保类型：1-内部担保
     */
    public static final Byte GUARANTEE_TYPE_INSIDE = 1;
    /**
     * 担保类型：2-银行担保
     */
    public static final Byte GUARANTEE_TYPE_BANK = 2;
    ////////////////////////////////////////////////// 担保类型 /////////////////////////////////////////////////////////


    ////////////////////////////////////////////////// 征信类型 /////////////////////////////////////////////////////////
    /**
     * 银行征信
     */
    public static final Byte CREDIT_TYPE_BANK = 1;
    /**
     * 社会征信
     */
    public static final Byte CREDIT_TYPE_SOCIAL = 2;
    ////////////////////////////////////////////////// 征信类型 /////////////////////////////////////////////////////////


    /////////////////////////////////////////////// 身份证过期日期 ///////////////////////////////////////////////////////
    /**
     * 身份证过期日期：长期
     */
    public static final String CUST_ID_CARD_EXPIRE_DATE = "长期";
    /////////////////////////////////////////////// 身份证过期日期 ///////////////////////////////////////////////////////


    ///////////////////////////////////////////////// 与主贷人关系 ///////////////////////////////////////////////////////

    // 与主贷人关系：0-本人;1-配偶;2-父母;3-子女;4-兄弟姐妹;5-亲戚;6-朋友;7-同学;8-同事;9-其它;

    public static final Byte CUST_RELATION_self = 0;

    public static final Byte CUST_RELATION_pei_ou = 1;

    public static final Byte CUST_RELATION_fu_mu = 2;

    public static final Byte CUST_RELATION_zi_nv = 3;

    public static final Byte CUST_RELATION_xiong_di_jie_mei = 4;

    public static final Byte CUST_RELATION_qin_qi = 5;

    public static final Byte CUST_RELATION_friend = 6;

    public static final Byte CUST_RELATION_tong_xue = 7;

    public static final Byte CUST_RELATION_tong_shi = 8;

    public static final Byte CUST_RELATION_other = 9;

    ///////////////////////////////////////////////// 与主贷人关系 ///////////////////////////////////////////////////////
}
