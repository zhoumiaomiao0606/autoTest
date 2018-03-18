package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/15
 */
@Data
public class AppInsuranceInfoVO {

    /**
     * 第几年
     */
    private Byte yearNum;

    /**
     * 商业保险公司
     */
    private String commercialInsuranceCompany;

    /**
     * 商业险截止日期
     */
    private Date commercialInsuranceEndDate;

    /**
     * 商业险保险金额
     */
    private BigDecimal commercialInsuranceAmount;

    /**
     * 交强险保险公司
     */
    private String trafficInsuranceCompany;

    /**
     * 交强险截止日期
     */
    private Date trafficInsuranceEndDate;

    /**
     * 交强险保险金额
     */
    private BigDecimal trafficInsuranceAmount;
}
