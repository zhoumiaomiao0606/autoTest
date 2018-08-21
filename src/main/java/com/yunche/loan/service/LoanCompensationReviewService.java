package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;

public interface LoanCompensationReviewService {
    /**
     * 代偿确认信息保存
     * @param param
     * @return
     */
    Void save(UniversalCompensationParam param);

    /**
     * 详情页
     * @param query
     * @return
     */
    ResultBean detail(UniversalCompensationQuery query);
}
