package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanBankCardSendDO;
import com.yunche.loan.domain.param.LoanBankCardSendExpParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoanBankCardSendDOMapper {

    int deleteByPrimaryKey(Long orderId);

    int insert(LoanBankCardSendDO record);

    int insertSelective(LoanBankCardSendDO record);

    LoanBankCardSendDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(LoanBankCardSendDO record);

    int updateByPrimaryKey(LoanBankCardSendDO record);

    int batchInsert(List<LoanBankCardSendDO> loanBankCardSendDOList);

    List<LoanBankCardSendDO> loanBankCardSendExp(LoanBankCardSendExpParam loanBankCardSendExpParam);

    int countByorderId(@Param("orderId")Long orderId);
}