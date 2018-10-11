package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class AppMortgageAndDataOverdueReportVO {

    private Long id;
    //业务员名
    private String name;
    //未抵押单数
    private String noMortgageSum;
    //未抵押贷款总额
    private String noMortgageAmountSum;
    //资料超期单数
    private String overdueSum;
    //资料超期贷款总额
    private String overdueAmountSum;

}
