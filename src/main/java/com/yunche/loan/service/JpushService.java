package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.FlowOperationMsgDO;

public interface JpushService {

    void push(FlowOperationMsgDO DO);

    ResultBean list(Integer pageIndex, Integer pageSize);

    void read(Long id);
}
