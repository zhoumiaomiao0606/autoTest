package com.yunche.loan.mapper.configure.info.car;

import com.yunche.loan.obj.configure.info.car.CarDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarDetailDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarDetailDO record);

    int insertSelective(CarDetailDO record);

    CarDetailDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarDetailDO record);

    int updateByPrimaryKey(CarDetailDO record);

    int batchInsert(List<CarDetailDO> carDetailDOS);

    List<Integer> getAllId();
}