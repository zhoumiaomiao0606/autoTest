package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.RiskCommitmentPara;

/**
 * @author liuzhe
 * @date 2018/11/7
 */
public interface LoanCommitKeyService {

    ResultBean<Void> riskUncollected(Long orderId);

    ResultBean letterOfRiskCommitment(RiskCommitmentPara riskCommitmentPara);

    ResultBean detail(Long orderId);

    ResultBean uncollected(Long orderId);

    ResultBean collected(Long e);
}
