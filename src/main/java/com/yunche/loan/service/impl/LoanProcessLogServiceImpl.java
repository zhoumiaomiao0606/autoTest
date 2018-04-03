package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.domain.entity.LoanProcessLogDO;
import com.yunche.loan.mapper.LoanProcessLogDOMapper;
import com.yunche.loan.service.LoanProcessLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuzhe
 * @date 2018/4/3
 */
@Service
public class LoanProcessLogServiceImpl implements LoanProcessLogService {


    @Autowired
    private LoanProcessLogDOMapper loanProcessLogDOMapper;


    /**
     * 获取订单任务节点的最新审核信息
     *
     * @param orderId
     * @param taskDefinitionKey
     * @return
     */
    @Override
    public LoanProcessLogDO getLoanProcessLog(Long orderId, String taskDefinitionKey) {
        Preconditions.checkNotNull(orderId, "订单ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(taskDefinitionKey), "任务节点不能为空");

        LoanProcessLogDO loanProcessLogDO = loanProcessLogDOMapper.lastLogByOrderIdAndTaskDefinitionKey(orderId, taskDefinitionKey);
        return loanProcessLogDO;
    }
}
