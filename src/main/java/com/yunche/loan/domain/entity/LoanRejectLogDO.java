package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanRejectLogDO {

    private Long id;

    private Long orderId;

    private String rejectOriginTask;

    private String rejectToTask;

    private Date gmtCreate;

    private String reason;

    private Byte opt;
}