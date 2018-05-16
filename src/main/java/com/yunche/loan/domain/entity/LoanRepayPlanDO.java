package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanRepayPlanDO extends LoanRepayPlanDOKey {
    private BigDecimal payableAmount;

    private BigDecimal actualRepayAmount;

    private Byte isOverdue;

    private BigDecimal overdueAmount;

    private Date checkDate;

    private Byte status;

    private Integer nper;


}