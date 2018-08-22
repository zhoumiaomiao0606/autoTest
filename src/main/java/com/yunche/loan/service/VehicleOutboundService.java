package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.VehicleOutboundDO;
import com.yunche.loan.domain.param.VehicleOutboundUpdateParam;
import com.yunche.loan.domain.vo.BaseCustomerInfoVO;
import com.yunche.loan.domain.vo.VehicleInfoVO;
import com.yunche.loan.domain.vo.VehicleOutboundInfo;
import com.yunche.loan.domain.vo.VehicleOutboundVO;

public interface VehicleOutboundService {
    VehicleOutboundVO detail(Long aLong,Long bank_repay_imp_record_id);

    ResultBean<Void> update(VehicleOutboundDO param);

    VehicleOutboundInfo vehicleOutbound(Long aLong, Long aLong1);

    BaseCustomerInfoVO customer(Long aLong,Long bank_repay_imp_record_id);

    VehicleInfoVO vehicleInfo(Long aLong);
}
