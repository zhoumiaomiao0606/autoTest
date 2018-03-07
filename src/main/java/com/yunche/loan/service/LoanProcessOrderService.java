package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanOrderDO; /**
 * @author liuzhe
 * @date 2018/3/6
 */
public interface LoanProcessOrderService {
    ResultBean<Long> create(LoanOrderDO loanOrderDO);
}
