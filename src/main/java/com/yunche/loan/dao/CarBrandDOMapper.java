package com.yunche.loan.dao;

import com.yunche.loan.domain.entity.CarBrandDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarBrandDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CarBrandDO record);

    int insertSelective(CarBrandDO record);

    CarBrandDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(CarBrandDO record);

    int updateByPrimaryKey(CarBrandDO record);

    int batchInsert(List<CarBrandDO> carBrandDOS);

    List<Integer> getAllId(@Param("status") Byte status);

    List<CarBrandDO> getAll(@Param("status") Byte status);

    List<String> getAllName(@Param("status") Byte status);
}