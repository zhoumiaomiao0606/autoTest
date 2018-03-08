package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCarInfoDO;
import com.yunche.loan.domain.param.LoanCarInfoParam;

/**
 * @author liuzhe
 * @date 2018/3/8
 */
public interface LoanCarInfoService {
    ResultBean<Long> create(LoanCarInfoDO loanCarInfoDO);

    ResultBean<Void> update(LoanCarInfoDO loanCarInfoDO);
}
