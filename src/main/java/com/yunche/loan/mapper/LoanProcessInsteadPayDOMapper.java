package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessInsteadPayDO;
import org.apache.ibatis.annotations.Param;

public interface LoanProcessInsteadPayDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanProcessInsteadPayDO record);

    int insertSelective(LoanProcessInsteadPayDO record);

    LoanProcessInsteadPayDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanProcessInsteadPayDO record);

    int updateByPrimaryKey(LoanProcessInsteadPayDO record);

    LoanProcessInsteadPayDO selectByOrderIdAndInsteadPayOrderIdReddlyReview(@Param("orderId") Long orderId,
                                                                            @Param("insteadPayOrderId") Long insteadPayOrderId);

    LoanProcessInsteadPayDO selectByOrderIdAndBankRepayImpRecordId(@Param("orderId") Long orderId,
                                                                   @Param("bankRepayImpRecordId") Long bankRepayImpRecordId);
}