package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.QueryObj.CarDetailQuery;
import com.yunche.loan.domain.dataObj.CarDetailDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarDetailDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CarDetailDO record);

    int insertSelective(CarDetailDO record);

    CarDetailDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(CarDetailDO record);

    int updateByPrimaryKey(CarDetailDO record);

    int batchInsert(List<CarDetailDO> carDetailDOS);

    List<Long> getAllId(@Param("status") Byte status);

    List<CarDetailDO> getAllIdAndModelId(@Param("status") Byte status);

    int count(CarDetailQuery query);

    List<CarDetailDO> query(CarDetailQuery query);

    List<CarDetailDO> getDetailListByModelId(@Param("modelId") Long modelId, @Param("status") Byte status);
}