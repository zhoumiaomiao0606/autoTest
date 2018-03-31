package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BankLendRecordDO {
    private Integer id;

    private Long loanOrder;

    private Date lendDate;

    private BigDecimal lendAmount;

    private Byte status;

    private Byte recordStatus;

    private Date gmtCreate;

    private String feature;
}