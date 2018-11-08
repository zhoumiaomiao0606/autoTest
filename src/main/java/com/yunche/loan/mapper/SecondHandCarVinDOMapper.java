package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.SecondHandCarVinDO;
import com.yunche.loan.domain.param.QueryVINParam;

import java.util.List;

public interface SecondHandCarVinDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SecondHandCarVinDO record);

    int insertSelective(SecondHandCarVinDO record);

    SecondHandCarVinDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SecondHandCarVinDO record);

    int updateByPrimaryKey(SecondHandCarVinDO record);

    List<SecondHandCarVinDO> queryVIN(QueryVINParam vin);
}