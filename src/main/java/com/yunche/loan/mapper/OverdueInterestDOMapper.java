package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.OverdueInterestDO;

public interface OverdueInterestDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(OverdueInterestDO record);

    int insertSelective(OverdueInterestDO record);

    OverdueInterestDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(OverdueInterestDO record);

    int updateByPrimaryKey(OverdueInterestDO record);
}