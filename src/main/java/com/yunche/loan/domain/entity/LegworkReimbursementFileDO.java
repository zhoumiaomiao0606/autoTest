package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LegworkReimbursementFileDO {
    private Long id;

    private Long legworkReimbursementId;

    private String urls;

    private Date gmtCreateTime;

    private Date gmtUpdateTime;
}