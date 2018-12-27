package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class HzBankNotMortgageVO {
    //合伙人编码
    private String partnerCode;
//合伙人名称
    private String pName;
//客户姓名
    private String cName;
//身份证号码
    private String idCard;
//贷款金额
    private String loanAmount;
//放款日期
    private String lendDate;
//合同邮寄日期
    private String infoCompanyToParDate;
//合同至银行日期
    private String contractToBankDate;
//银行盖章时效
    private String bankSealDays;
//已邮寄天数
    private String postDays;
//未抵押天数
    private String notMortgageDays;
//超期范围
    private String extendedRange;
//上牌地
    private String areaName;
//抵押渠道状态
    private String mortgageChannelStatus;
//超期原因类型
    private String overdueTypeReason;
//跟进情况
    private String followSituation;
}
