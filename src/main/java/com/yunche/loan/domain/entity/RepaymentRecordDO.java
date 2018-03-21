package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RepaymentRecordDO extends RepaymentRecordDOKey {
    private Long bizOrder;

    private String userName;

    private BigDecimal optimalReturn;

    private BigDecimal minPayment;

    private BigDecimal pastDue;

    private Integer cumulativeOverdueTimes;

    private BigDecimal cardBalance;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;


}