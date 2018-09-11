package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BusinessApprovalReportVO {
    private Long id;

    private String cName;

    private String idCArd;

    private String sName;

    private String pName;

    private String hName;

    private String gDate;

    private String remitAmount;

    private String loanAmount;

    private String signRate;

    private String bankPeriodPrincipal;

}
