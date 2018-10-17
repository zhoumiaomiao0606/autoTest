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
     * 第三方过桥资金-KEY
     */
    public static final Set<String> LOAN_PROCESS_BRIDGE_PAY_KEYS = Sets.newHashSet(
            BRIDGE_HANDLE.getCode(),
            BRIDGE_REPAY_RECORD.getCode(),
//            BRIDGE_INTEREST_RECORD.getCode(),
            BRIDGE_REPAY_INFO.getCode()
    );

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
     * 上门催收流程-KEY
     */
    public static final Set<String> LOAN_PROCESS_COLLECTION_KEYS = Sets.newHashSet(
            VISIT_COLLECTION_REVIEW.getCode(),
            VISIT_COLLECTION.getCode(),
            CAR_HANDLE.getCode(),
            CAR_OUT.getCode(),
            SETTLE_ORDER.getCode()
    );

    /**
     * 法务处理流程-KEY
     */
    public static final Set<String> LOAN_PROCESS_LEGAL_KEYS = Sets.newHashSet(
            LEGAL_REVIEW.getCode(),
            LEGAL_RECORD.getCode()
    );

    /**
     * 流程外的节点
     */
    public static final Set<String> OUTSIDE_LOAN_PROCESS_KEYS = Sets.newHashSet(
            INFO_SUPPLEMENT.getCode(),
            CREDIT_SUPPLEMENT.getCode(),
            FINANCIAL_SCHEME_MODIFY_APPLY.getCode(),
            FINANCIAL_SCHEME_MODIFY_APPLY_REVIEW.getCode(),
            REFUND_APPLY.getCode(),
            REFUND_APPLY_REVIEW.getCode(),
            CUSTOMER_REPAY_PLAN_RECORD.getCode(),
            COLLECTION_WORKBENCH.getCode(),
            OUTWORKER_COST_APPLY.getCode(),
            OUTWORKER_COST_APPLY_REVIEW.getCode(),
            OVERDUE_COLLECTION_LIST.getCode()
    );

    /**
     * 审核-无需order_id
     */
    public static final Set<String> APPROVAL_NOT_NEED_ORDER_ID_PROCESS_KEYS = Sets.newHashSet(
            // 一个报销单 --> N个order_id
            OUTWORKER_COST_APPLY.getCode(),
            OUTWORKER_COST_APPLY_REVIEW.getCode()
    );
}
