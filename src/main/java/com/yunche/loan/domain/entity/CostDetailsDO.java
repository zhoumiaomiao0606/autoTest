package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CostDetailsDO {
    private Long id;

    private BigDecimal service_fee;

    private BigDecimal apply_license_plate_deposit_fee;

    private BigDecimal performance_fee;

    private BigDecimal install_gps_fee;

    private BigDecimal risk_fee;

    private BigDecimal fair_assess_fee;

    private BigDecimal apply_license_plate_out_province_fee;

    private BigDecimal based_margin_fee;

    private Byte service_fee_type;

    private Byte apply_license_plate_deposit_fee_type;

    private Byte performance_fee_type;

    private Byte install_gps_fee_type;

    private Byte risk_fee_type;

    private Byte fair_assess_fee_type;

    private Byte apply_license_plate_out_province_fee_type;

    private Byte based_margin_fee_type;

    private BigDecimal extra_fee;

    private Byte extra_fee_type;

    private BigDecimal other_fee;

    private Byte other_fee_type;

    private Byte status;

    private String feature;

    private String listrule;

    private String  rebateFirst;   //第一道返利

    private String rebateSecond;   //第二道返利

    private BigDecimal rebate_not_deducted;

    private BigDecimal partner_rebate_amount;//合伙人返利金额

    private Byte key_risk_premium_type;
}