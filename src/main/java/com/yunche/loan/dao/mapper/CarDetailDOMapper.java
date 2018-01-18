package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.QueryObj.CarDetailQuery;
import com.yunche.loan.domain.dataObj.CarDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarDetailDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CarDetailDO record);

    int insertSelective(CarDetailDO record);

    CarDetailDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CarDetailDO record);

    int updateByPrimaryKey(CarDetailDO record);

    int batchInsert(List<CarDetailDO> carDetailDOS);

    List<Long> getAllId();

    List<CarDetailDO> getAllIdAndModelId();

    int count(CarDetailQuery query);

    List<CarDetailDO> query(CarDetailQuery query);

    List<CarDetailDO> getDetailListByModelId(Long modelId);
}