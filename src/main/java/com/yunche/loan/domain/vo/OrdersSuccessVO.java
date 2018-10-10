package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class OrdersSuccessVO {
    //征信未通过
    private String creditNoPass;
    //风控未通过
    private String riskNoPass;
    //抵押未完成
    private String mortgageNoComplete;
    //退款客户
    private String refund;
    //成功客户
    private String success;
    //其他
    private String other;
}
