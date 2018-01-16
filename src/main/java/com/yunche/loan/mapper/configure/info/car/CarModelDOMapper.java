package com.yunche.loan.mapper.configure.info.car;

import com.yunche.loan.obj.configure.info.car.CarModelDO;
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