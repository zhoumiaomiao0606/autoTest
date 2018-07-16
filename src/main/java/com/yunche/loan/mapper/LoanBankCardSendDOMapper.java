package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanBankCardSendDO;

public interface LoanBankCardSendDOMapper {

    int deleteByPrimaryKey(Long orderId);

    int insert(LoanBankCardSendDO record);

    int insertSelective(LoanBankCardSendDO record);

    LoanBankCardSendDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(LoanBankCardSendDO record);

    int updateByPrimaryKey(LoanBankCardSendDO record);
}