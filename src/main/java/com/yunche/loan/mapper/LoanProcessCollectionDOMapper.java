package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessCollectionDO;

public interface LoanProcessCollectionDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanProcessCollectionDO record);

    int insertSelective(LoanProcessCollectionDO record);

    LoanProcessCollectionDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanProcessCollectionDO record);

    int updateByPrimaryKey(LoanProcessCollectionDO record);
}