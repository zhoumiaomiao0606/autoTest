package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ThirdPartyFundBusinessDO {
    private Long orderId;

    private Date lendDate;

    private Byte repayType;

    private Date repayDate;

    private String repayRemark;

    private BigDecimal interest;

    private BigDecimal poundage;

    private Date repayInterestDate;

    private String repayRegisterRemark;

    private Date gmtCreate;

    private Date gmtModify;

   }