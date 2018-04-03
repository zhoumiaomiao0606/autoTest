package com.yunche.loan.service;

import com.yunche.loan.domain.entity.LoanProcessLogDO;

/**
 * @author liuzhe
 * @date 2018/4/3
 */
public interface LoanProcessLogService {

    LoanProcessLogDO getLoanProcessLog(Long orderId, String taskDefinitionKey);
}
