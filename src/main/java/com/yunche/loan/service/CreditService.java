package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.dataObj.LoanBaseInfoDO;
import com.yunche.loan.domain.queryObj.OrderListQuery;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.domain.viewObj.LoanBaseInfoVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/2/26
 */
public interface CreditService {

    ResultBean<Long> createLoanBaseInfo(String orderId, LoanBaseInfoDO loanBaseInfoDO);

    ResultBean<Void> updateLoanBaseInfo(LoanBaseInfoDO loanBaseInfoDO);

    ResultBean<LoanBaseInfoVO> getLoanBaseInfoById(Long id);

    ResultBean<List<InstLoanOrderVO>> query(OrderListQuery query);
}
