package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;

public interface LoanPartnerCompensationReviewService {
    /**
     * 保存
     * @param param
     * @return
     */
    ResultBean save(UniversalCompensationParam param);

    /**
     * 详情
     * @param query
     * @return
     */
    ResultBean detail(UniversalCompensationQuery query);
}
