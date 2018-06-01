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

    private Byte status;

    private String feature;
    /**
     * 保险情况
     */
    private String insurance_situation;

    private String payment_organization;//付款组织

    private Date application_date;//申请日期

    private Date gmt_create;

    private String remark;
}