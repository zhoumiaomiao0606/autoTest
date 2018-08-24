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

    LoanProcessCollectionDO selectByPrimaryOrderIdAndBankRepayImpRecordId(@Param("orderId") Long orderId,@Param("bank_repay_imp_record_id")Long bank_repay_imp_record_id);

    LoanProcessCollectionDO getLastLoanProcessByOrderIdAndBankRepayImpRecordId(@Param("orderId") Long orderId,
                                                                               @Param("bankRepayImpRecordId") Long bankRepayImpRecordId);
}