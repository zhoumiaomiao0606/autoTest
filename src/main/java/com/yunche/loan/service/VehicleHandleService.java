package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.VehicleHandleUpdateParam;
import com.yunche.loan.domain.vo.VehicleHandleVO;

public interface VehicleHandleService {
    VehicleHandleVO detail(Long aLong,Long bankRepayImpRecordId);

    ResultBean<Void> update(VehicleHandleUpdateParam param);
}
