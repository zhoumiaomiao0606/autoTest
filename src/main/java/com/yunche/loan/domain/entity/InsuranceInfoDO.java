package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class InsuranceInfoDO {
    private Long id;

    private Long order_id;

    private Date issue_bills_date;

    private Byte insurance_year;

    private Byte status;

    private String feature;

}