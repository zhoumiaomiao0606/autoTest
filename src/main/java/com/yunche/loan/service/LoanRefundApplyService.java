package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanRefundApplyParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;

import java.util.List;

public interface LoanRefundApplyService {


    RecombinationVO detail(Long orderId, Long refundId);

    ResultBean<Long> update(LoanRefundApplyParam param);

    List<UniversalCustomerOrderVO> queryRefundCustomerOrder(String name);
}
