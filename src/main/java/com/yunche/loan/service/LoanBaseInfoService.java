package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.LoanBaseInfoVO; /**
 * @author liuzhe
 * @date 2018/3/6
 */
public interface LoanBaseInfoService {
    ResultBean<Void> update(LoanBaseInfoVO loanBaseInfoVO);
}
