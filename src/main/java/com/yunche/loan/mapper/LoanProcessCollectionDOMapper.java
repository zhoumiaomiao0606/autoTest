package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessCollectionDO;
import org.apache.ibatis.annotations.Param;

public interface LoanProcessCollectionDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanProcessCollectionDO record);

    int insertSelective(LoanProcessCollectionDO record);

    LoanProcessCollectionDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanProcessCollectionDO record);

    int updateByPrimaryKey(LoanProcessCollectionDO record);

    LoanProcessCollectionDO getLastLoanProcessByOrderIdAndBankRepayImpRecordId(@Param("orderId") Long orderId,
                                                                               @Param("bankRepayImpRecordId") Long bankRepayImpRecordId);
}