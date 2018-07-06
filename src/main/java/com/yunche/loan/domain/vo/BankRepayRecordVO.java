package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankRepayRecordVO {

    private String  name ;
    private String  credentialNo;
    private String  cardNumber;
    private BigDecimal cardBalance;
    private BigDecimal  optimalReturn;
    private Integer      consecutiveBreachNumber;
    private Integer cumulativeBreachNumber;
    private Byte isCustomer;
}
