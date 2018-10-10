package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class AppBusinessDetailReportVO {
    //订单号
    private Long orderId;
    //姓名
    private String name;
    //车型
    private String carType;
    //贷款本金
    private String loanAmount;
    //执行利率
    private String signRate;
    //按揭期限
    private String loanTime;
    //分期本金
    private String bankPeriodPrincipal;
    //业务员名
    private String sName;

}
