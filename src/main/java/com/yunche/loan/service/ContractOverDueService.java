package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ContractOverDueParam;

public interface ContractOverDueService
{
    ResultBean list(ContractOverDueParam param);

}
