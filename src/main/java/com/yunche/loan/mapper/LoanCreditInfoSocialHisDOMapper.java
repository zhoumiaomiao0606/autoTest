package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCreditInfoSocialHisDO;

public interface LoanCreditInfoSocialHisDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanCreditInfoSocialHisDO record);

    int insertSelective(LoanCreditInfoSocialHisDO record);

    LoanCreditInfoSocialHisDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanCreditInfoSocialHisDO record);

    int updateByPrimaryKey(LoanCreditInfoSocialHisDO record);

    LoanCreditInfoSocialHisDO lastByCustomerId(Long customerId);
}