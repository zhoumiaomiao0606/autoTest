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

//    private Long salesmanId;

    private String salesmanName;

    private String partnerName;

    private String bank;

    private String carName;
    /**
     * 车辆类型：1-新车; 2-二手车; 3-不限;
     */
    private Byte carType;
    /**
     * 车牌号
     */
    private String licensePlateNumber;
    /**
     * 垫资日期
     */
    private String xxxxDate;
    /**
     * 上牌日期
     */
    private String licensePlateDate;
    /**
     * 上跑抵押日期
     */
    private String licensePlateDepositDate;
    /**
     * 银行放款日期
     */
    private String bankLendDate;
    /**
     * 车价
     */
    private BigDecimal carPrice;
    /**
     * 贷款额
     */
    private BigDecimal loanAmount;
    /**
     * 每月还款日期
     */
    private Date xxxDate;
    /**
     * 首月还款额
     */
    private BigDecimal firstMonthRepay;
    /**
     * 每月还款
     */
    private BigDecimal eachMonthRepay;
    /**
     * 按揭期限
     */
    private Integer loanTime;
    /**
     * 还款总额
     */
    private BigDecimal total;
    /**
     * GPS个数
     */
    private Integer gpsNum;
    /**
     * 履约保证金
     */
    private BigDecimal Money;
    /**
     * 贷款状态
     */
    private Byte loanStatus;
    /**
     * 还款卡号
     */
    private String payAccount;

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
