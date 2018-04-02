package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ApplyLicensePlateDepositInfoDO {

    private Long id;

    private Date apply_license_plate_deposit_date;

    private Byte status;

    private String feature;

}