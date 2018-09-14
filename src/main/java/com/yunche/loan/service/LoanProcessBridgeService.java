package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;

/**
 * @author liuzhe
 * @date 2018/9/11
 */
public interface LoanProcessBridgeService {

    ResultBean<Void> approval(ApprovalParam approval);

    Long startProcess(Long orderId);
}
