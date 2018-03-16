package com.yunche.loan.config.constant;

/**
 * APP端 -多任务节点查询类型
 *
 * @author liuzhe
 * @date 2018/3/16
 */
public class AppMultipartQueryTypeConst {
    /**
     * 1-贷款申请【待审核】;
     */
    public static final Integer LOAN_APPLY_TODO = 1;
    /**
     * 2-贷款申请【已审核】
     */
    public static final Integer LOAN_APPLY_DONE = 2;
    /**
     * 3-客户查询【在贷客户】
     */
    public static final Integer CUSTOMER_LOAN_TODO = 3;
    /**
     * 4-客户查询【已贷客户】
     */
    public static final Integer CUSTOMER_LOAN_DONE = 4;
}
