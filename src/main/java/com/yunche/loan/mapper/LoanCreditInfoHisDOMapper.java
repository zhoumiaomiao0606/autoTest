package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCreditInfoHisDO;

public interface LoanCreditInfoHisDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanCreditInfoHisDO record);

    int insertSelective(LoanCreditInfoHisDO record);

    LoanCreditInfoHisDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanCreditInfoHisDO record);

    int updateByPrimaryKey(LoanCreditInfoHisDO record);

    LoanCreditInfoHisDO lastByCustomerId(Long customerId);
}