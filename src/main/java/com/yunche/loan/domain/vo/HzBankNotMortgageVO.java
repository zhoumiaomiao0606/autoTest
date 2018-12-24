package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class HzBankNotMortgageVO {
    private String partnerCode;

    private String pName;

    private String cName;

    private String idCard;

    private String loanAmount;

    private String lendDate;

    private String infoCompanyToParDate;

    private String contractToBankDate;

    private String bankSealDays;

    private String postDays;

    private String notMortgageDays;

    private String extendedRange;

    private String areaName;

    private String mortgageChannelStatus;

    private String overdueTypeReason;

    private String followSituation;
}
