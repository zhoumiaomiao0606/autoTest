package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessDO {
    private Long orderId;

    private Byte creditApply;

    private Byte creditApplyVerify;

    private Byte bankCreditRecord;

    private Byte socialCreditRecord;

    private Byte loanApply;

    private Byte visitVerify;

    private Byte telephoneVerify;

    private Byte carInsurance;

    private Byte applyLicensePlateDepositInfo;

    private Byte installGps;

    private Byte commitKey;

    private Byte vehicleInformation;

    private Byte materialReview;

    private Byte materialPrintReview;

    private Byte businessReview;

    private Byte loanReview;

    private Byte remitReview;

    private Date gmtCreate;

    private Date gmtModify;

    private String cancelTaskDefKey;

    private String loanApplyRejectOrginTask;
}