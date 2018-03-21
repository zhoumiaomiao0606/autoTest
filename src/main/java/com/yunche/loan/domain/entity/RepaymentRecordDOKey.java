package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class RepaymentRecordDOKey {
    private String idCard;

    private String repayCardId;

    private Integer currentOverdueTimes;


}