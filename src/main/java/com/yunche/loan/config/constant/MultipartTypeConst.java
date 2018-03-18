package com.yunche.loan.config.constant;

/**
 * 多节点查询类型：
 * 1-征信申请列表【未查询：   [提交征信申请单后  ,   贷款业务申请单)        】;
 * 2-征信申请列表【已查询：   [贷款业务申请单    ,   end]                 】;
 * 3-贷款申请列表【待审核：   [提交贷款申请单后  ,   电审通过)             】;
 * 4-贷款申请列表【已审核：   [电审通过后       ,    end]                】;
 * 5-客户查询列表【在贷客户： [提交征信申请单后  ,    未放款)              】;
 * 6-客户查询列表【已贷客户： [已放款           ,    end]                】;
 *
 * @author liuzhe
 * @date 2018/3/16
 */
public class MultipartTypeConst {
    /**
     * 1-征信申请列表【未查询：   [提交征信申请单后  ,   贷款业务申请单)        】;
     */
    public static final Integer MULTIPART_TYPE_CREDIT_APPLY_TODO = 1;

    /**
     * 2-征信申请列表【已查询：   [贷款业务申请单    ,   end]                 】;
     */
    public static final Integer MULTIPART_TYPE_CREDIT_APPLY_DONE = 2;

    /**
     * 3-贷款申请列表【待审核：   [提交贷款申请单后  ,   电审通过)             】;
     */
    public static final Integer MULTIPART_TYPE_LOAN_APPLY_TODO = 3;

    /**
     * 4-贷款申请列表【已审核：   [电审通过后       ,    end]                】;
     */
    public static final Integer MULTIPART_TYPE_LOAN_APPLY_DONE = 4;

    /**
     * 5-客户查询列表【在贷客户： [提交征信申请单后  ,    未放款)              】;
     */
    public static final Integer MULTIPART_TYPE_CUSTOMER_LOAN_TODO = 5;

    /**
     * 6-客户查询列表【已贷客户： [已放款           ,    end]                】;
     */
    public static final Integer MULTIPART_TYPE_CUSTOMER_LOAN_DONE = 6;
}
