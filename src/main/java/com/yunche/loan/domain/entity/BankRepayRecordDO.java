package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class BankRepayRecordDO extends BankRepayRecordDOKey {
    private String userName;

    private String idCard;

    private String repayCard;

    private BigDecimal cardBalance;

    private BigDecimal overdueAmount;

    private Integer overdueTimes;

    private Integer maxOverdueTimes;

    private Byte isCustomer;

    private Date batchDate;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;


}