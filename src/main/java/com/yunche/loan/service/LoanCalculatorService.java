package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;

import java.math.BigDecimal;

public interface LoanCalculatorService {

    public ResultBean getAllProduct(String bankName);

    public ResultBean cal(Long prodId, BigDecimal loanAmt, BigDecimal exeRate, int loanTime, BigDecimal carPrice);

}
