package com.yunche.loan.service;

import com.yunche.loan.domain.vo.RecombinationVO;

public interface RepaymentPlanService {
    public RecombinationVO detail(Long orderId);
}
