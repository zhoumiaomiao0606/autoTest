package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCustomerDO;

public interface LoanCustomerDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanCustomerDO record);

    int insertSelective(LoanCustomerDO record);

    LoanCustomerDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanCustomerDO record);

    int updateByPrimaryKey(LoanCustomerDO record);
}