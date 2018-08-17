package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.VehicleOutboundDO;
import com.yunche.loan.domain.param.VehicleOutboundUpdateParam;
import com.yunche.loan.domain.vo.VehicleOutboundVO;

public interface VehicleOutboundService {
    VehicleOutboundVO detail(Long aLong);

    ResultBean<Void> update(VehicleOutboundDO param);
}
