package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessLegalDO;
import org.apache.ibatis.annotations.Param;

public interface LoanProcessLegalDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanProcessLegalDO record);

    int insertSelective(LoanProcessLegalDO record);

    LoanProcessLegalDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanProcessLegalDO record);

    int updateByPrimaryKey(LoanProcessLegalDO record);

    LoanProcessLegalDO getLastLoanProcessByOrderIdAndBankRepayImpRecordId(@Param("orderId") Long orderId,
                                                                               @Param("bankRepayImpRecordId") Long bankRepayImpRecordId);
}