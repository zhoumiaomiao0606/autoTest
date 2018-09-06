package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ConfThirdPartyMoneyDO {

    private Long id;

    private String name;

    private String contact;

    private String mobile;

    private BigDecimal cautionMoney;

    private BigDecimal yearRate;

    private BigDecimal singleCost;

    private Long bankId;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;
}