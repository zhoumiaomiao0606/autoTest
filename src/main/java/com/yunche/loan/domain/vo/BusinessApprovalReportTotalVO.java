package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BusinessApprovalReportTotalVO {
    private BigDecimal remitAmount;

    private BigDecimal loanAmount;

    private BigDecimal bankPeriodPrincipal;
}
