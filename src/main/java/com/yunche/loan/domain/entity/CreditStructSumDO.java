package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreditStructSumDO {

    private Long id;

    private Date firstCardSendMonth;

    private BigDecimal averageRepayLastMonth6;

    private BigDecimal averageCostLastMonth6;

    private Date gmtCreate;

    private Date gmtModify;
}