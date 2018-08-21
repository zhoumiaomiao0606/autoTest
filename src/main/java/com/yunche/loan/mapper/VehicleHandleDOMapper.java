package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VehicleHandleDO;
import com.yunche.loan.domain.entity.VehicleHandleDOKey;

public interface VehicleHandleDOMapper {
    int deleteByPrimaryKey(VehicleHandleDOKey key);

    int insert(VehicleHandleDO record);

    int insertSelective(VehicleHandleDO record);

    VehicleHandleDO selectByPrimaryKey(VehicleHandleDOKey key);

    int updateByPrimaryKeySelective(VehicleHandleDO record);

    int updateByPrimaryKey(VehicleHandleDO record);
}