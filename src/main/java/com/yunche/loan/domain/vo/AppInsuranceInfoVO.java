package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
     * 商业险列表
     */
    private List<InsuranceDetail> commercialInsuranceList;
    /**
     * 交强险列表
     */
    private List<InsuranceDetail> trafficInsuranceList;

    private List<InsuranceDetail> vehicleVesselTaxInsuranceList;


    @Data
    public static class InsuranceDetail {
        /**
         * 保单号
         */
        private String insuranceNumber;

        /**
         * 保险公司
         */
        private String insuranceCompany;

        /**
         * 保险开始日期
         */
        private Date insuranceStartDate;

        /**
         * 保险截止日期
         */
        private Date insuranceEndDate;

        /**
         * 保险金额
         */
        private BigDecimal insuranceAmount;
    }
}
