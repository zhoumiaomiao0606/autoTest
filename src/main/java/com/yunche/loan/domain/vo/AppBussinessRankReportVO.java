package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class AppBussinessRankReportVO {
    //业务员名
    private String name;
    //业务单数
    private String bussNum;
    //贷款总额
    private String totalAmount;
    //单笔贷款额
    private String averageAmount;

}
