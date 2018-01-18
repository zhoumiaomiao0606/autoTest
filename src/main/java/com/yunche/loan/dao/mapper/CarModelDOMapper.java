package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.QueryObj.CarModelQuery;
import com.yunche.loan.domain.dataObj.CarModelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarModelDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CarModelDO record);

    int insertSelective(CarModelDO record);

    CarModelDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CarModelDO record);

    int updateByPrimaryKey(CarModelDO record);

    int batchInsert(List<CarModelDO> carModelDOS);

    List<Long> getAllId();

    List<CarModelDO> getModelListByBrandId(Long brandId);

    int count(CarModelQuery query);

    List<CarModelDO> query(CarModelQuery query);

    List<String> getNameListByBrandId(Long brandId);
}