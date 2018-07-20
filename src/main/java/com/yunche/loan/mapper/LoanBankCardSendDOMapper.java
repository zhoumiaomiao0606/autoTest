package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanBankCardSendDO;

import java.util.List;

public interface LoanBankCardSendDOMapper {

    int deleteByPrimaryKey(Long orderId);

    int insert(LoanBankCardSendDO record);

    int insertSelective(LoanBankCardSendDO record);

    LoanBankCardSendDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(LoanBankCardSendDO record);

    int updateByPrimaryKey(LoanBankCardSendDO record);

    int batchInsert(List<LoanBankCardSendDO> loanBankCardSendDOList);
}