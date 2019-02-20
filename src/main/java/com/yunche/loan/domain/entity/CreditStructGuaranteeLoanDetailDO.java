package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreditStructGuaranteeLoanDetailDO {

    private Long id;

    private Long customerId;

    private BigDecimal loanAmount;

    private Integer period;

    private Date expireDate;

    private Byte accountStatus;

    private Date gmtCreate;

    private Date gmtModify;
}