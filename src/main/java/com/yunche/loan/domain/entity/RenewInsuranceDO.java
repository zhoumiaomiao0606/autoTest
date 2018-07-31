package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RenewInsuranceDO {
    private Long id;

    private Long orderId;

    private Date remindDate;

    private Byte renewInsuranceStatus;

    private String autoInsuEmployee;

    private BigDecimal busiInsur;

    private String sign;

    private BigDecimal damageInsur;

    private BigDecimal damageInsurFee;

    private BigDecimal thirdDutyInsur;

    private BigDecimal thirdDutyInsurFee;

    private BigDecimal robberyInsur;

    private BigDecimal autoignitionInsur;

    private BigDecimal glassInsur;

    private BigDecimal notDeductInsur;

    private BigDecimal strongInsur;

    private BigDecimal vesselTax;

    private BigDecimal insurEmployeeTel;

    private String remark;

    private String message;

    private BigDecimal persLiabilityInsur;

    private BigDecimal persLiabilityInsurFee;

    private BigDecimal totalPremium;


}