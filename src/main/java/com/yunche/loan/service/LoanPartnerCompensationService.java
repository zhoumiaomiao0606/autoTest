package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;

/**
 * 合伙人代偿
 */
public interface LoanPartnerCompensationService {
    /**
     * 合伙人代偿
     * @param universalCompensationParam
     * @return
     */
    Void save(UniversalCompensationParam universalCompensationParam);

    /**
     * 详情
     * @param query
     * @return
     */
    ResultBean detail(UniversalCompensationQuery query);
}
