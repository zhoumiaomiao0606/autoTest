package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessLegalDO;

public interface LoanProcessLegalDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanProcessLegalDO record);

    int insertSelective(LoanProcessLegalDO record);

    LoanProcessLegalDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanProcessLegalDO record);

    int updateByPrimaryKey(LoanProcessLegalDO record);

    LoanProcessLegalDO getLastLoanProcessByBankRepayImpRecordId(Long bankRepayImpRecordId);
}