package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanTelephoneVerifyParam;

/**
 * @author liuzhe
 * @date 2018/4/12
 */
public interface LoanTelephoneVerifyService {
    ResultBean<Void> save(LoanTelephoneVerifyParam loanTelephoneVerifyParam);
}
