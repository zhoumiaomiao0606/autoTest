package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class InsuranceInfoDO {
    private Long id;

    private Long order_id;

    private Date issue_bills_date;

    private String residential_address;

    private Byte status;

    private String feature;

}