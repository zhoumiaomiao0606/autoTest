package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VehicleOutboundDO;
import com.yunche.loan.domain.entity.VehicleOutboundDOKey;

public interface VehicleOutboundDOMapper {
    int deleteByPrimaryKey(VehicleOutboundDOKey key);

    int insert(VehicleOutboundDO record);

    int insertSelective(VehicleOutboundDO record);

    VehicleOutboundDO selectByPrimaryKey(VehicleOutboundDOKey key);

    int updateByPrimaryKeySelective(VehicleOutboundDO record);

    int updateByPrimaryKey(VehicleOutboundDO record);
}