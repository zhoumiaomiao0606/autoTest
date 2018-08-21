package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class FileInfoDO {
    private Long id;

    private String type;

    private Long orderId;

    private Long bankRepayImpRecordId;

    private String remark;

    private String path;
}