package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UniversalCompensationVO{

    private String orderId;

    private Date applyCompensationDate;

    private BigDecimal currArrears;

    private BigDecimal loanBanlance;

    private BigDecimal advancesBanlance;

    private Integer overdueDays;

    //逾期次数
    private Integer overdueNumber;

    private Integer advancesNumber;

    private BigDecimal riskTakingRatio;

    private BigDecimal compensationAmount;

    private String compensationCause;

    private String outCarNumber;

    private String outBank;

    private String outAccount;

    private String receiveBank;

    private String receiveCarNumber;

    private String receiveAccount;

    private Date reviewDate;

    private String reviewOperator;

    private String partnerCompensationOperator;

    private Date partnerOperationDate;

    private BigDecimal partnerCompensationAmount;

    private String partnerDcReviewOperator;

    private Date partnerDcReviewDate;

    private Byte status;

    private String remark;

    private Date gmtCreate;

    private Date gmtModify;
}
