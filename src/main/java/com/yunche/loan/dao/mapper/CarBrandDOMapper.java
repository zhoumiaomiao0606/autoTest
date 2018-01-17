package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.CarBrandDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarBrandDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarBrandDO record);

    int insertSelective(CarBrandDO record);

    CarBrandDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBrandDO record);

    int updateByPrimaryKey(CarBrandDO record);

    int batchInsert(List<CarBrandDO> carBrandDOS);

    List<Integer> getAllId();
}