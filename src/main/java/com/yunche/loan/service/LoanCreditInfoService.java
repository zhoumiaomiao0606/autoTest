package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCreditInfoDO;
import com.yunche.loan.domain.param.CreditRecordParam;
import com.yunche.loan.domain.vo.LoanCreditInfoVO;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
public interface LoanCreditInfoService {
    ResultBean<Long> create(LoanCreditInfoDO loanCreditInfoDO);

    ResultBean<Long> update(LoanCreditInfoDO loanCreditInfoDO);

    ResultBean<LoanCreditInfoVO> getByCustomerId(Long id, Byte type);
}
