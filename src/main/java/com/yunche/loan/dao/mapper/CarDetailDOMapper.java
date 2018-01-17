package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.CarDetailDO;
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

    List<CarDetailDO> getAllIdAndModelId();
}