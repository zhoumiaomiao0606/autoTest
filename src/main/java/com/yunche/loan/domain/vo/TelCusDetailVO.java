package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class TelCusDetailVO {
    //订单号
    private String orderId;
    //客户名
    private String cusName;
    //身份证
    private String idCard;
    //银行
    private String bank;
    //团队
    private String pName;
    //业务员
    private String sName;
    //车辆类型
    private String carType;
    //车型
    private String carName;
    //车系
    private String modelName;
    //车价
    private String carPrice;
    //时间
    private String createTime;
}
