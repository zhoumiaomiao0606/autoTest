package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liuzhe
 * @date 2018/6/23
 */
@Data
public class VideoFaceQuestionAnswerVO {

    private String customerName;

    private String customerIdCard;

    private String carName;

    private String carBrandName;

    private BigDecimal carPrice;
    /**
     * 贷款金额
     */
    private BigDecimal loanAmount;
    /**
     * 每月还款
     */
    private BigDecimal eachMonthRepay;
    /**
     * 贷款期数
     */
    private Integer loanTime;
    /**
     * 总还款        -> 本息合计
     */
    private BigDecimal principalInterestSum;
}
