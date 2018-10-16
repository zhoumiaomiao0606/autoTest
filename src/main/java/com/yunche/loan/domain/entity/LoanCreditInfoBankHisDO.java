package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanCreditInfoBankHisDO {

    private Long id;

    private Long customerId;

    private Date creditApplyTime;

    private Long creditApplyUserId;

    private String creditApplyUserName;

    private Date bankCreditRecordTime;

    private Long bankCreditRecordUserId;

    private String bankCreditRecordUserName;

    private Byte bankCreditResult;

    private Date bankCreditRejectTime;

    private Long bankCreditRejectUserId;

    private String bankCreditRejectUserName;

    private String bankCreditRejectInfo;
}