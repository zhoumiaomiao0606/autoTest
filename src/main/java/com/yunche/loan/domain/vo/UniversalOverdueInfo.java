package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalOverdueInfo {
    private String repay_overdue_amount;
    private String repay_overdue_times;
    private String repay_max_overdue_times;
    private String repay_compensatory;
}
