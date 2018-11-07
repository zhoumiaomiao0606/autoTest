package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;

/**
 * @author liuzhe
 * @date 2018/11/7
 */
public interface LoanCommitKeyService {

    ResultBean<Void> uncollected(Long orderId);
}
