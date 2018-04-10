package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/15
 */
@Data
public class AppBusinessInfoVO {
    /**
     * 业务员 1
     */
    private String salesmanName;
    /**
     * 业务团队1
     */
    private String partnerName;
    /**
     * 业务组织
     */
    private String departmentName;
    /**
     * 贷款银行
     */
    private String bank;
    /**
     * 还款卡号
     */
    private String repayCardId;
    /**
     * 车辆价格 1
     */
    private BigDecimal carPrice;
    /**
     * 基准评估价
     */
    private BigDecimal appraisal;
    /**
     * 产品大类
     */
    private  String  categorySuperior;
    /**
     * 车辆类型：1-新车; 2-二手车; 3-不限;
     */
    private Byte carType;
    /**
     * 贷款产品
     */
    private  String  prodName;

    /**
     * 贷款期限1
     */
    private int loanTime;
    /**
     * 执行利率1
     */
    private BigDecimal signRate;

    /**
     * 首付额1
     */
    private BigDecimal downPaymentMoney;
    /**
     * 首付比例1
     */
    private BigDecimal downPaymentRatio;
    /**
     * 银行分期本金
     */
    private  BigDecimal bankPeriodPrincipal;
    /**
     * 银行分期比例
     */
    private BigDecimal stagingRatio;


    /**
     * +=============还款明细========================
     */
    /**
     * 首月还款
     */
    private BigDecimal  firstMonthRepay;
    /**
     * 月还款
     */
    private BigDecimal eachMonthRepay;
    /**
     * 还款总额
     */
    private BigDecimal totalRepayment;
    /**
     * 贷款利息
     */
    private BigDecimal loanInterest;
    /**
     * 车型名称
     */
    private  String  carDetailName;
    /**
     * 车辆属性
     */
    private  Byte  vehicleProperty;
    /**
     * 车辆类别 carType
     */

    /**
     * 行驶证车主
     */
    private  String  nowDrivingLicenseOwner;
    /**
     * 上牌方式
     */
    private Byte licensePlateType;
    /**
     * 上牌地点
     */
    private String applyLicensePlateArea;
    /**
     * 车辆颜色
     */
    private String color;

    /**
     * 留备用钥匙loan_car_info
     */
    private   Byte carKey;
    /**
     * 业务来源  loan_car_info
     */
    private Byte  businessSource;
    /**
     * 二手车初登日期 loan_car_info
     */
       private  Date    firstRegisterDate;
    /**
     * 上牌日期 vehicle_information
     */
    private  Date   applyLicensePlateDate;
//    /**
//     * 牌证齐全日期 material_audit;
//     */
//    private Date  completeMaterialDate;
    /**
     * 抵押日期
     */
    private Date applyLicensePlateDepositDate;
    /**
     * 备注
     */
    private  String info;

    private String carName;

    /**
     * 车辆类型文本值：1-新车; 2-二手车; 3-不限;
     */
    private String carTypeText;
    /**
     * 车牌号
     */
    private String licensePlateNumber;
    /**
     * 上牌日期
     */
    private Date licensePlateDate;
    /**
     * 上牌抵押日期
     */
    private Date licensePlateDepositDate;

    /**
     * 贷款额
     */
    private BigDecimal loanAmount;

//    /**
//     * 首月还款额
//     */
//    private BigDecimal firstMonthRepay;
//    /**
//     * 每月还款
//     */
//    private BigDecimal eachMonthRepay;
//    /**
//     * 按揭期限
//     */
//    private Integer loanTime;
    /**
     * 还款总额
     */
    private BigDecimal totalRepay;
    /**
     * GPS个数
     */
    private Integer gpsNum;
    /**
     * 履约保证金
     */
    private BigDecimal performanceMoney;
    /**
     * 还款卡号
     */
    private String repayAccount;

    /**
     * 银行征信结果
     */
    private Byte bankCreditResult;
    /**
     * 银行征信备注
     */
    private String bankCreditInfo;
    /**
     * 社会征信结果
     */
    private Byte socialCreditResult;
    /**
     * 社会征信备注
     */
    private String socialCreditInfo;


}
