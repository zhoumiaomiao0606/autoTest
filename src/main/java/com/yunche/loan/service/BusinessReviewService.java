package com.yunche.loan.service;

import com.yunche.loan.domain.param.BusinessReviewCalculateParam;
import com.yunche.loan.domain.param.BusinessReviewUpdateParam;

import java.math.BigDecimal;
import java.util.Map;

public interface BusinessReviewService {
    public Map detail(Long orderId);

    public void update(BusinessReviewUpdateParam param);

    public BigDecimal calculate(BusinessReviewCalculateParam param);
}
