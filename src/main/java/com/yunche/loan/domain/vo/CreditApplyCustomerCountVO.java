package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class CreditApplyCustomerCountVO {
    private Long creditApplyCustomerCount;

    private Long creditRecordCount;

    private Long loanInfoRecordCount;

    private Long visitVerifyRecordCount;
}
