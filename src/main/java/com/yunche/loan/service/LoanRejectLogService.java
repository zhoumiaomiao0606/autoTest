package com.yunche.loan.service;

import com.yunche.loan.domain.entity.LoanRejectLogDO;

/**
 * @author liuzhe
 * @date 2018/4/16
 */
public interface LoanRejectLogService {
    LoanRejectLogDO rejectLog(Long orderId, String taskDefinitionKey);
}
