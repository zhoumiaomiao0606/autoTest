package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FinancialRebateDetailDO extends FinancialRebateDetailDOKey {
    private BigDecimal rebateAmount;

    private Byte enterAccountFlag;

    private Date gmtCreate;

    private Date gmtModify;
}