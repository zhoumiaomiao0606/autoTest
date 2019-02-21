package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CreditStructSumDO;

public interface CreditStructSumDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CreditStructSumDO record);

    int insertSelective(CreditStructSumDO record);

    CreditStructSumDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CreditStructSumDO record);

    int updateByPrimaryKey(CreditStructSumDO record);
}