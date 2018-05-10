package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalLoanRepaymentPlan {

    private String repay_period;
    private String repay_order_id;
    private String repay_date;
    private String repay_payable_amount;
    private String repay_actual_repay_amount;
    private String repay_is_overdue;
    private String repay_overdue_amount;
    private String repay_check_date;
}
