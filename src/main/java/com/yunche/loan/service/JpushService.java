package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.FlowOperationMsgDO;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

public interface JpushService {

    public void push(FlowOperationMsgDO DO);

    public ResultBean list(Integer pageIndex, Integer pageSize);

    public void read(Long id);
}
