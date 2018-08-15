package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanApplyCompensationDO extends LoanApplyCompensationDOKey {
    private BigDecimal currArrears;

    private BigDecimal loanBanlance;

    private BigDecimal advancesBanlance;

    private Integer overdueDays;

    private Integer overdueNumber;

    private Integer advancesNumber;

    private BigDecimal riskTakingRatio;

    private BigDecimal compensationAmount;

    private String compensationCause;

    private String outCarNumber;

    private String outBank;

    private String outAccount;

    private Date reviewDate;

    private String reviewOperator;

    private String partnerCompensationOperator;

    private Date partnerOperationDate;

    private BigDecimal partnerCompensationAmount;

    private Byte status;

    private String remark;

    private Date gmtCreate;

    private Date gmtModify;


}