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
    /**
     * 保险情况
     */
    private String insurance_situation;
    /**
     * 付款组织
     */
    @Deprecated
    private String payment_organization;
    /**
     * 业务付款-申请日期
     */
    private Date application_date;
    /**
     * 打款时间
     */
    private Date remit_time;
    /**
     * 备注
     */
    private String remark;

    private Date gmt_create;

    private Date gmt_modify;

    private Byte status;

    private String feature;
}