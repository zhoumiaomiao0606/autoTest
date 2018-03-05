package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.CarModelQuery;
import com.yunche.loan.domain.entity.CarModelDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarModelDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CarModelDO record);

    int insertSelective(CarModelDO record);

    CarModelDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(CarModelDO record);

    int updateByPrimaryKey(CarModelDO record);

    int batchInsert(List<CarModelDO> carModelDOS);

    List<Long> getAllId(@Param("status") Byte status);

    List<CarModelDO> getModelListByBrandId(@Param("brandId") Long brandId, @Param("status") Byte status);

    int count(CarModelQuery query);

    List<CarModelDO> query(CarModelQuery query);

    List<String> getNameListByBrandId(@Param("brandId") Long brandId, @Param("status") Byte status);

    List<CarModelDO> getAll(@Param("status") Byte status);
}