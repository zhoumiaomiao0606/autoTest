package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.RemitDetailsParam;
import com.yunche.loan.domain.vo.RecombinationVO;

public interface FinanceService {
    public RecombinationVO detail(Long orderId);

    ResultBean update(RemitDetailsParam remitDetailsParam);

    ResultBean getAccount();
}
