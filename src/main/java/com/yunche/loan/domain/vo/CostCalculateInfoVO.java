package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CostCalculateInfoVO {
    //银行分期本金
    private BigDecimal bank_period_principal;
    //是否月结 0 否 1 是
    private String pay_month;

}
