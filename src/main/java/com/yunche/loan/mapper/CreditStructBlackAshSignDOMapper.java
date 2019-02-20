package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CreditStructBlackAshSignDO;

public interface CreditStructBlackAshSignDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CreditStructBlackAshSignDO record);

    int insertSelective(CreditStructBlackAshSignDO record);

    CreditStructBlackAshSignDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CreditStructBlackAshSignDO record);

    int updateByPrimaryKey(CreditStructBlackAshSignDO record);
}