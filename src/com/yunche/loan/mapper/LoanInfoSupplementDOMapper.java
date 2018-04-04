package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanInfoSupplementDO;

public interface LoanInfoSupplementDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanInfoSupplementDO record);

    int insertSelective(LoanInfoSupplementDO record);

    LoanInfoSupplementDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanInfoSupplementDO record);

    int updateByPrimaryKey(LoanInfoSupplementDO record);
}