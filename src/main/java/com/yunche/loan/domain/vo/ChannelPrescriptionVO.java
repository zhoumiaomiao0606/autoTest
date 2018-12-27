package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ChannelPrescriptionVO {
    //垫款时间
    private String remitReviewDate;
//纸审通过时间
    private String materialReviewDate;
//交银行时间
    private String sendDate;
//银行合同收到时间
    private String receiveDate;
//大区
    private String bizArea;
//编码
    private String partnerCode;
//合伙人
    private String pName;
//姓名
    private String cName;
//身份证号
    private String idCard;
//合同本金
    private String bankPeriodPrincipal;
//放款时间
    private String lendDate;
//纸审时效
    private String paperPrescription;
//放款时效
    private String loanPrescription;
//回款时效
    private String moneyBackPrescription;

}
