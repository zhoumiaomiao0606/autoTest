package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessInsteadPayDO;

public interface LoanProcessInsteadPayDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanProcessInsteadPayDO record);

    int insertSelective(LoanProcessInsteadPayDO record);

    LoanProcessInsteadPayDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanProcessInsteadPayDO record);

    int updateByPrimaryKey(LoanProcessInsteadPayDO record);
}