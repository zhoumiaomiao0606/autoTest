package com.yunche.loan.config.constant;

import com.google.common.collect.Sets;

import java.util.Set;

import static com.yunche.loan.config.constant.LoanProcessEnum.*;

/**
 * @author liuzhe
 * @date 2018/8/21
 */
public class LoanProcessConst {

    /**
     * 代偿流程-KEY
     */
    public static final Set<String> LOAN_PROCESS_INSTEAD_PAY_KEYS = Sets.newHashSet(
            APPLY_INSTEAD_PAY.getCode(),
            FINANCE_INSTEAD_PAY_REVIEW.getCode(),
            PARTNER_INSTEAD_PAY.getCode(),
            PARTNER_INSTEAD_PAY_REVIEW.getCode()
    );

    /**
     * 催收流程-KEY
     */
    public static final Set<String> LOAN_PROCESS_COLLECTION_KEYS = Sets.newHashSet(
            COLLECTION_WORKBENCH.getCode(),
            VISIT_COLLECTION_REVIEW.getCode(),
            VISIT_COLLECTION.getCode(),
            CAR_HANDLE.getCode(),
            CAR_OUT.getCode(),
            SETTLE_ORDER.getCode(),
            LEGAL_REVIEW.getCode(),
            LEGAL_RECORD.getCode()
    );

    /**
     * 流程外的节点
     */
    public static final Set<String> OUTSIDE_LOAN_PROCESS_NODE_LIST = Sets.newHashSet(
            INFO_SUPPLEMENT.getCode(),
            CREDIT_SUPPLEMENT.getCode(),
            FINANCIAL_SCHEME_MODIFY_APPLY.getCode(),
            FINANCIAL_SCHEME_MODIFY_APPLY_REVIEW.getCode(),
            REFUND_APPLY.getCode(),
            REFUND_APPLY_REVIEW.getCode(),
            CUSTOMER_REPAY_PLAN_RECORD.getCode(),
            COLLECTION_WORKBENCH.getCode(),
            OUTWORKER_COST_APPLY.getCode(),
            OUTWORKER_COST_APPLY_REVIEW.getCode()
    );
}
