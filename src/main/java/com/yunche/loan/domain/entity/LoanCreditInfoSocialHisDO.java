package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanCreditInfoSocialHisDO {

    private Long id;

    private Long customerId;

    private Date creditApplyTime;

    private Long creditApplyUserId;

    private String creditApplyUserName;

    private Date socialCreditRecordTime;

    private Long socialCreditRecordUserId;

    private String socialCreditRecordUserName;

    private Byte socialCreditResult;

    private Date socialCreditRejectTime;

    private Long socialCreditRejectUserId;

    private String socialCreditRejectUserName;

    private String socialCreditRejectInfo;
}