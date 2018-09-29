package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PartnerCompensations
{
    private BigDecimal compensatoryAmount;//代偿金额
    private Date compensatoryTime;//代偿时间
}
