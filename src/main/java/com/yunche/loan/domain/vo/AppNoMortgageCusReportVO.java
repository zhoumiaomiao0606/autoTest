package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class AppNoMortgageCusReportVO {
    //订单号
    private Long orderId;
    //客户名
    private String name;
    //车型
    private String carName;
    //业务员名
    private String sName;
    //贷款本金
    private String loanAmount;
    //垫款时间
    private String loanTime;

}
