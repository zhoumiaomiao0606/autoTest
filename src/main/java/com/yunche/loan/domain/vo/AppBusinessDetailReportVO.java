package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class AppBusinessDetailReportVO {
    private Long orderId;

    private String NAME;

    private String carType;

    private String signRate;

    private String loanAmount;

    private String bankPeriodPrincipal;

    private String telephoneVerify;

    private String remitReview;

    private String createTime;

}
