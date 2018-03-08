package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanBaseInfoParam;
import com.yunche.loan.domain.vo.LoanBaseInfoVO;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
public interface LoanBaseInfoService {
    ResultBean<Void> update(LoanBaseInfoParam param);

    ResultBean<LoanBaseInfoVO> getLoanBaseInfoById(Long loanBaseInfoId);

    ResultBean<Long> create(LoanBaseInfoParam param);
}
