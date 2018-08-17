package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VehicleOutboundDO;

public interface VehicleOutboundDOMapper {
    int deleteByPrimaryKey(Long orderid);

    int insert(VehicleOutboundDO record);

    int insertSelective(VehicleOutboundDO record);

    VehicleOutboundDO selectByPrimaryKey(Long orderid);

    int updateByPrimaryKeySelective(VehicleOutboundDO record);

    int updateByPrimaryKey(VehicleOutboundDO record);
}