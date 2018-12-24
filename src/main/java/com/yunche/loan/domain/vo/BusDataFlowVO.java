package com.yunche.loan.domain.vo;

import lombok.Data;
@Data
public class BusDataFlowVO {
    //银行
    private String bank;
//编码
    private String partnerCode;
//大区
    private String bizArea;
//业务员
    private String eName;
//客户姓名
    private String cName;
//身份证
    private String idCard;
//抄单员
    private String copyClerk;
//分期期数
    private String loanTime;
//新/二
    private String carType;
//合同本金
    private String bankPeriodPrincipal;
//贷款本金
    private String loanAmount;
//服务费
    private String serviceCharge;
//合同收到时间
    private String expressReceiveDate;
//合同时间
    private String contractTime;
//合同交银行时间
    private String expressSendDate;
//上牌地
    private String areaName;
//是否线上
    private String isOnline;
//银行放款时间
    private String lendDate;
//退单合同
    private String refundDate;

}
