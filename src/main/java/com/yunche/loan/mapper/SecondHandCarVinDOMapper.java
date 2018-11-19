package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.SecondHandCarVinDO;

public interface SecondHandCarVinDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SecondHandCarVinDO record);

    int insertSelective(SecondHandCarVinDO record);

    SecondHandCarVinDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SecondHandCarVinDO record);

    int updateByPrimaryKey(SecondHandCarVinDO record);
}