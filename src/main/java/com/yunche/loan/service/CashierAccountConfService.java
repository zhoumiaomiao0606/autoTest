package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CashierAccountConfParam;
import com.yunche.loan.domain.param.QueryCashierAccountConfParam;

public interface CashierAccountConfService {
    public ResultBean<Long> create(CashierAccountConfParam cashierAccountConfParam);

    ResultBean<Void> update(CashierAccountConfParam cashierAccountConfParam);

    ResultBean<Void> delete(Long id);

    ResultBean listAll(QueryCashierAccountConfParam queryCashierAccountConfParam);

    ResultBean listAllEmployName();

    ResultBean listAllCreateUserName();

    ResultBean listAllCashierAccountConfByEmployeeId(Long employeeId);
}
