package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreditStructTradeDetailLoanDO {

    private Long id;

    private Long customerId;

    private Byte type;

    private BigDecimal loanAmount;

    private BigDecimal principalBalance;

    private BigDecimal currentMonthReapy;

    private Integer period;

    private Date expireDate;

    private Byte repayWay;

    private Integer overdueTotalNumLastMonth12;

    private Integer overdueMaxNumLastMonth12;

    private Integer overdueTotalNumLastMonth24;

    private Integer overdueMaxNumLastMonth24;

    private BigDecimal currentOverdueMoney;

    private Byte accountStatus;

    private Byte accountType;

    private Date gmtCreate;

    private Date gmtModify;
}