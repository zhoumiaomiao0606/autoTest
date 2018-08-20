package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;
@Data
public class ForceDO {
    private Long id;

    private Date registerDate;

    private String registerId;

    private String summary;

    private String ruleCourt;

    private String executionApplicant;

    private String executor;

    private String undertakeJudge;

    private String undertakeTarget;

    private String propertyClues;

    private Date suspensionDate;

    private Date endDate;

    private String courtAcceptTarget;

    private String undertakeRemarks;

    private String repaidNum;

    private String repaidMoney;

    private String repaidInterest;

    private String surplusNum;

    private String surplusMoney;

    private String surplusInterest;

    private Date secondDate;

    private String secondTarget;

    private String secondRemarks;

    private String forceRemarks;

    private Long orderId;

    private Long bankRepayImpRecordId;
}