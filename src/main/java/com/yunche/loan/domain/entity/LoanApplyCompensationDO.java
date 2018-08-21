package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanApplyCompensationDO extends LoanApplyCompensationDOKey {

    //逾期金额
    private BigDecimal currArrears;

    private BigDecimal loanBanlance;

    private BigDecimal advancesBanlance;

    private Integer overdueDays;

    //逾期次数
    private Integer overdueNumber;

    private Integer advancesNumber;

    private BigDecimal riskTakingRatio;

    //财务代偿金额
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

    //合伙人代偿确认经办时间
    private Date partnerDcReviewDate;

    private Byte status;

    private String remark;

    private Date gmtCreate;

    private Date gmtModify;


}