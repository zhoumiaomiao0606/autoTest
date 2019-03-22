package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class YuncheBoardDO {
    private Integer id;

    private Date applyTime;

    private String applyMan;

    private String applyPartment;

    private String bank;

    private Byte level;

    private String title;

    private Date effectiveTime;

    private Date startTime;

    private Date endTime;

    private String urls;

    private Byte status;

    private String content;
}