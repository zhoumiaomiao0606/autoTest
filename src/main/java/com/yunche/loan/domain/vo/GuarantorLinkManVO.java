package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-12 11:38
 * @description:
 **/
@Data
public class GuarantorLinkManVO
{
    //客户姓名
    private String linkManName;
    //关联人证件号码
    private String linkManIdCard;
    //性别
    private String linkManSex;
    //证件是否长期有效
    private String booleanLongTerm;
    //证件有效期截止日
    private String linkManIdentityValidity;
    //与申请人关系
    private String linkManREapplyPerson;
    //个人年收入
    private BigDecimal linkManYearIncome;
    //住宅地址
    private String linkManAddress;
    //单位名称
    private String linkManIncomeCertificateCompanyName;
    //单位地址
    private String linkManIncomeCertificateCompanyAddress;
    //手机号
    private String linkManMobile;
}
