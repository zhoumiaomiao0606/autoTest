package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CostDetailsDO;

public interface CostDetailsDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(CostDetailsDO record);

    CostDetailsDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CostDetailsDO record);

}