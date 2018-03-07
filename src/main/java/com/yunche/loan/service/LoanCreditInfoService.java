package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CreditRecordParam;
import com.yunche.loan.domain.vo.LoanCreditInfoVO;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
public interface LoanCreditInfoService {
    ResultBean<Long> create(CreditRecordParam creditRecordParam);

    ResultBean<Long> update(CreditRecordParam creditRecordParam);

    ResultBean<LoanCreditInfoVO> getByCustomerId(Long id, Byte type);
}
