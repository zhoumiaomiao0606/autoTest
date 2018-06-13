package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import com.yunche.loan.domain.vo.LoanBaseInfoVO;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
public interface LoanBaseInfoService {

    ResultBean<Void> update(LoanBaseInfoDO loanBaseInfoDO);

    ResultBean<LoanBaseInfoVO> getLoanBaseInfoById(Long loanBaseInfoId);

    ResultBean<Long> create(LoanBaseInfoDO loanBaseInfoDO);

    Long getBankId(Long orderId);
}
