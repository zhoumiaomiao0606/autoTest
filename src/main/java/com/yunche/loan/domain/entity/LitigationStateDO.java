package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class LitigationStateDO {
    private Long id;

    private String isstop;

    private String stopReason;

    private String collectionType;

    private String bankRepayImpRecordId;
}