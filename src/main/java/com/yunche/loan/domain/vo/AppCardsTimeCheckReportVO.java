package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class AppCardsTimeCheckReportVO {
    //业务员名
    private String sName;
    //客户名
    private String name;
    //车型
    private String carName;
    //车牌
    private String carId;
    //垫款时间
    private String makeAdvancesTime;
    //上牌时间
    private String onTheCardsTime;
    //间隔天数
    private String differTime;
}
