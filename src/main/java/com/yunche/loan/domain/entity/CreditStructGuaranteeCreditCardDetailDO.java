package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreditStructGuaranteeCreditCardDetailDO {

    private Long id;

    private Long customerId;

    private Date openAccountDate;

    private BigDecimal creditLine;

    private Byte accountStatus;

    private Date gmtCreate;

    private Date gmtModify;
}