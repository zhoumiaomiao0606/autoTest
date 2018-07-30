package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class InsuranceRiskDO {

    private Long id;

    private String insuranceNumber;

    private String insuranceCompanyName;

    private Date riskDate;

    private Date acceptDate;

    private BigDecimal totalMoney;

    private BigDecimal applyClamisMoney;

    private String clamisCommissioner;
}