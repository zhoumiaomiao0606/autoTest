package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VehicleHandleDO;

public interface VehicleHandleDOMapper {
    int deleteByPrimaryKey(Long orderid);

    int insert(VehicleHandleDO record);

    int insertSelective(VehicleHandleDO record);

    VehicleHandleDO selectByPrimaryKey(Long orderid);

    int updateByPrimaryKeySelective(VehicleHandleDO record);

    int updateByPrimaryKey(VehicleHandleDO record);
}