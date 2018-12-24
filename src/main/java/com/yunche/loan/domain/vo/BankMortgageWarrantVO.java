package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class BankMortgageWarrantVO {
    //合伙人
    private String pName;
//主贷姓名
    private String cName;
//身份证号
    private String idCard;
//抵押材料公司至合伙人日期
    private String postDate;
//已邮寄天数
    private String postDays;
//按揭银行
    private String bank;
//银行放款本金
    private String money;
//放款日期
    private String unsecuredDate;
//未抵押天数
    private String unsecuredDays;
//超期范围
    private String overdue;
//上牌地
    private String areaName;
//车牌号
    private  String number;

}
