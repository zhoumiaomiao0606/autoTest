package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-10 15:14
 * @description:
 **/
@Data
public class ExportCustomerInfoVO extends ExportCustomerIdVO
{
    //姓名
    private String pName;
    //姓名拼音
    private String pyName;
    //性别
    private String pSex;
    //出生日期
    private String pBirth;
    //证件号码
    private String pIdCard;
    //证件有效截止日
    private String pIdentityValidity;
    //国籍  --默认中国
    private String pNationnality ="中国";
    //婚姻状况
    private String pMarry;
    //教育程度
    private String pEducation;
    //手机号
    private String pMobile;
    //住宅地址
    private String pAddress;
    //住宅电话
    private String pHomeMobile;
    //邮编
    private String pPostcode;
    //住宅状况
    private String pHouseFeature;
    //单位名称
    private String pIncomeCertificateCompanyName;
    //单位地址
    private String pIncomeCertificateCompanyAddress;
    //邮编
    private String pCompanyPostcode;
    //单位电话
    private String pCompanyPhone;
    //单位经济性质
    private String pCompanyNature;
    //所属行业
    private String pIndustryCategory;
    //职业
    private String pOccupation;
    //职务
    private String pDuty;
    //年收入
    private BigDecimal pYearIncome;



//父类中
    //亲属联系人2姓名
    //关系
    //手机号

    //首付款
    private String pDownPaymentMoney;
    //贷款金额
    private String pLoanAmount;
    //贷款期限
    private String pLoanTime;
    //还款人月均总收入
    private BigDecimal pMonthIncome;

    //个人总资产---计算
    private BigDecimal totalAsset;

    //进口车标志
    private String pVehicleProperty;
    //生产厂商
    private String pCooperationDealer;
    //汽车品牌
    private String brandName;
    //款式规格
    private String modelName;
    //购车年月
    private String purchaseDate;
    //车牌号码
    private String licensePlateNumber;
    //车架号
    private String vehicleIdentificationNumber;
    //发动机号
    private String engineNumber;
    //汽车办理抵押地区
    private String applyLicensePlateArea;


    //汽车权属人姓名
    private String pcarPropertyOwner="系统无该字段";
    //申请人与抵押物权属人关系
    private String applyPersonReMortgage="系统无该字段";

    //合同套打时间
    private String materialPrintTime;

    //合同套打提交人
    private String materialPrintSubmitUser;

//父类
    //关联人证件号码
    //客户姓名
    //性别
    //证件是否长期有效
    //证件有效期截止日
    //与申请人关系
    //个人年收入
    //住宅地址
    //单位名称
    //单位地址
    //手机号
}
