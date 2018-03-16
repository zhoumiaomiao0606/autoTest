package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.FinancialProductDO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FinancialProductParam extends FinancialProductDO {

    /**
     * 银行费率列表
     */
    private List<ProductRate> productRateList;

    @Data
    public static class ProductRate {

        private BigDecimal bankRate;

        private Integer loanTime;
    }
}
