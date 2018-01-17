package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.CarModelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarModelDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarModelDO record);

    int insertSelective(CarModelDO record);

    CarModelDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarModelDO record);

    int updateByPrimaryKey(CarModelDO record);

    int batchInsert(List<CarModelDO> carModelDOS);

    List<Integer> getAllId();
}