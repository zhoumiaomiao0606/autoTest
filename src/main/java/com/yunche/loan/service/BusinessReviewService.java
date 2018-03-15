package com.yunche.loan.service;

import com.yunche.loan.domain.param.BusinessReviewCalculateParam;
import com.yunche.loan.domain.param.BusinessReviewUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;

import java.math.BigDecimal;

public interface BusinessReviewService {
    public RecombinationVO detail(Long orderId);

    public void update(BusinessReviewUpdateParam param);

    public BigDecimal calculate(BusinessReviewCalculateParam param);
}
