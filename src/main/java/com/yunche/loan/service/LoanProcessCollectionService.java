package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;

/**
 * @author liuzhe
 * @date 2018/8/20
 */
public interface LoanProcessCollectionService {

    ResultBean<Void> approval(ApprovalParam approval);
}
