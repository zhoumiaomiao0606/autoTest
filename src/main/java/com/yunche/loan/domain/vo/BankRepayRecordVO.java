package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankRepayRecordVO {

    private String  userName ;
    private String  idCard;
    private String  repayCard;
    private BigDecimal cardBalance;
    private BigDecimal  overdueAmount;
    private Integer      overdueTimes;
    private Integer maxOverdueTimes;
    private Byte isCustomer;
}
