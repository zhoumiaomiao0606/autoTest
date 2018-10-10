package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class AppMakeMoneyDetailReportVO {
    //订单号
    private Long orderId;
    //客户名
    private String NAME;
    //执行利率
    private String signRate;
    //贷款本金
    private String loanAmount;
    //分期本金
    private String bankPeriodPrincipal;
    //垫款状态
    private String remitReview;
    //垫款金额
    private String remitAmount;
    //垫款申请时间
    private String createTime;

}
