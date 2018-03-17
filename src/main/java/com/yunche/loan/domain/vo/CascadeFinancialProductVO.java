package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/15
 */
@Data
public class CascadeFinancialProductVO {

    // 银行
    private List<Bank> bankList;

    @Data
    public static class Bank {

        private String bank;
        /**
         * 金融产品列表
         */
        private List<FinancialProduct> financialProductList;
    }

    @Data
    public static class FinancialProduct {
        /**
         * 金融产品ID
         */
        private Long id;
        /**
         * 金融产品名称
         */
        private String name;

        /**
         * 银行利率列表
         */
        private List<BankRate> bankRateList;
    }

    @Data
    public static class BankRate {
        /**
         * 期数
         */
        private Integer loanTime;
        /**
         * 银行利率
         */
        private BigDecimal bankRate;
    }
}
