package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RemitDetailsDO {
    private Long id;

    private String beneficiary_bank;

    private String beneficiary_account;

    private String beneficiary_account_number;

    private BigDecimal remit_amount;

    private BigDecimal return_rate_amount;

    private String insurance_situation;

    private String remark;

    private String payment_organization;

    private Date application_date;

    private Date remit_time;

    private Date gmt_create;

    private Date gmt_modify;

    private Byte status;

    private String feature;

    private String remit_bank;

    private String remit_account;

    private String remit_account_number;

    private Byte remit_status;

}