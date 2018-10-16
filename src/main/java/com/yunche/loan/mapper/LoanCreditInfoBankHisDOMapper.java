package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCreditInfoBankHisDO;

public interface LoanCreditInfoBankHisDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanCreditInfoBankHisDO record);

    int insertSelective(LoanCreditInfoBankHisDO record);

    LoanCreditInfoBankHisDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanCreditInfoBankHisDO record);

    int updateByPrimaryKey(LoanCreditInfoBankHisDO record);

    LoanCreditInfoBankHisDO lastByCustomerId(Long principalCustId);
}