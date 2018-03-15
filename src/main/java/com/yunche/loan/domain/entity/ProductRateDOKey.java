package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRateDOKey {
    private Long prodId;

    private BigDecimal bankRate;

    private Integer loanTime;
}