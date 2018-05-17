package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class BankRepayRecordDOKey {
    private Long orderId;

    private Long bankRepayImpRecordId;

    private String idCard;

    private String repayCard;


}