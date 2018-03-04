package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanHomeVisitDO {
    private Long id;

    private Long visitSalesmanId;

    private Date visitDate;

    private String surveyReport;

    private String visitAddress;

    private String files;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;
}