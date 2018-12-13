package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ContractOverDueParam;
import com.yunche.loan.domain.vo.ContractOverDueDetailVO;

public interface ContractOverDueService
{
    ResultBean list(ContractOverDueParam param);

    String exportContractOverDue(ContractOverDueParam param);

    ContractOverDueDetailVO detail(Long orderId);
}
