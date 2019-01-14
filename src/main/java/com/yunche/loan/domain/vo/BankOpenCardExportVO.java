package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class BankOpenCardExportVO {
    private String id;

    private String partnerCode;

    private String parName;

    private String cusName;

    private String idCard;
    private String bank;
    private String loanAmount;
    private String bankPeriodPrincipal;

    private String loanTime;

    private String requestTime;

    private String result;




}
