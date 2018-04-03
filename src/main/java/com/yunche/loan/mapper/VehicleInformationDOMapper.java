package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VehicleInformationDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VehicleInformationDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(VehicleInformationDO record);

    VehicleInformationDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VehicleInformationDO record);

}