package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class AppMakeMoneyDetailReportVO {
    private Long orderId;

    private String NAME;

    private String signRate;

    private String loanAmount;

    private String bankPeriodPrincipal;

    private String remitReview;

    private String remitAmount;

    private String createTime;

}
