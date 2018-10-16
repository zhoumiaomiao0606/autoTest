package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class AppDataTimeCheckReportVO {
    //业务员名
    private String sName;
    //客户名
    private String name;
    //车型
    private String carName;
    //垫款时间
    private String makeAdvancesTime;
    //合同资料寄出
    private String contractTime;
    //间隔天数
    private String differTime;
}
