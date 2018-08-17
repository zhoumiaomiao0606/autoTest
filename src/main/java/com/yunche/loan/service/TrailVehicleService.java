package com.yunche.loan.service;
/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 09:34
 * @description:
 **/

import com.yunche.loan.domain.param.TrailVehicleUpdateParam;
import com.yunche.loan.domain.vo.TrailVehicleDetailVO;

public interface TrailVehicleService
{
    TrailVehicleDetailVO detail(Long orderId);

    void update(TrailVehicleUpdateParam param);
}
