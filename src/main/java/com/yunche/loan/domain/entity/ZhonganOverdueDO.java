package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class ZhonganOverdueDO {
    private Long id;

    private String overdueCounts;

    private String overdueMoney;

    private String platformCode;

    private String overdueTime;


}