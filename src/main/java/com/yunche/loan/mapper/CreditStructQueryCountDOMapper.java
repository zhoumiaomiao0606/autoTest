package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CreditStructQueryCountDO;

public interface CreditStructQueryCountDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CreditStructQueryCountDO record);

    int insertSelective(CreditStructQueryCountDO record);

    CreditStructQueryCountDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CreditStructQueryCountDO record);

    int updateByPrimaryKey(CreditStructQueryCountDO record);
}