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


    /**
     * 钥匙风险金比例
     */
    private Integer keyRiskPremium;

    /**
     * 钥匙风险金-确认---钥匙风险金---默认-0-未确认--1-确认
     */
    private Byte keyRiskPremiumConfirm;

    private Byte passLevel;

    private String passTime;

    private String passName;
}