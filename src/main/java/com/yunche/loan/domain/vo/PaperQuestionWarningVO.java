package com.yunche.loan.domain.vo;

import lombok.Data;


@Data
public class PaperQuestionWarningVO {
    //纸审人员
    private String nName;
//大区
    private String bizArea;
//合伙人编码
    private String partnerCode;
//合伙人
    private String pName;
//客户名字
    private String cName;
//身份证号码
    private String idCard;
//合同本金
    private String bankPeriodPrincipal;
//纸审日期
    private String nTime;
//垫款日期
    private String submitTime;
//基本资料齐全时间
    private String completeDate;
//超期天数
    private String overdueDays;
//银行
    private String bank;

}
