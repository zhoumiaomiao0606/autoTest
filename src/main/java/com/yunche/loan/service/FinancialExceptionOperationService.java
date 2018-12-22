package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.FinancialExceptionOperationParam;

public interface FinancialExceptionOperationService
{
    ResultBean list(FinancialExceptionOperationParam financialExceptionOperationParam);
}
