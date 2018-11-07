package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanTelephoneVerifyDO {

    private String orderId;

    private String info;

    private Date gmtCreate;

    private Date gmtModify;

    private String userName;

    private Long userId;

    /**
     * 风险分担加成
     */
    private BigDecimal riskSharingAddition;
}