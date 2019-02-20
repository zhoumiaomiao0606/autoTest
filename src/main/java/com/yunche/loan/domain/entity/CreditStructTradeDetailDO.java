package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreditStructTradeDetailDO {

    private Long id;

    private Long customerId;

    private Byte isIcbcCreditCard;

    private Date openAccountDate;

    private BigDecimal creditLine;

    private BigDecimal totalUseLine;

    private BigDecimal averageUseLineLastMonth6;

    private Integer overdueTotalNumLastMonth12;

    private Integer overdueMaxNumLastMonth12;

    private Integer overdueTotalNumLastMonth24;

    private Integer overdueMaxNumLastMonth24;

    private BigDecimal currentOverdueMoney;

    private Byte accountStatus;

    private Date gmtCreate;

    private Date gmtModify;
}