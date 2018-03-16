package com.yunche.loan.config.util;


import java.math.BigDecimal;

public class CostCalculate {
    //银行分期本金
    private BigDecimal bank_period_principal;
    //返利金额
    private BigDecimal return_rate_amount;

    public CostCalculate(BigDecimal bank_period_principal,BigDecimal return_rate_amount){

        this.bank_period_principal = bank_period_principal;
        this.return_rate_amount = return_rate_amount;

    }

    public CostCalculate process(String type,BigDecimal fee){
        //1 打款内扣 2 返利内扣 3 实收
        if("1".equals(type) || "3".equals(type)){
            bank_period_principal = bank_period_principal.subtract(fee);
        }else if("2".equals(type)){
            return_rate_amount = return_rate_amount.subtract(fee);
        }
        return this;
    }

    public BigDecimal finalResult(){
        return bank_period_principal.subtract(return_rate_amount);
    }
}
