package com.yunche.loan.service;

import com.yunche.loan.domain.param.ApplyLicensePlateDepositInfoUpdateParam;
import com.yunche.loan.domain.param.LoanRefundApplyParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;

import java.util.List;

public interface LoanRefundApplyService {


    public RecombinationVO detail(Long orderId,Long refundId);

    public void update(LoanRefundApplyParam param);

    List<UniversalCustomerOrderVO> queryRefundCustomerOrder(String name);
}
