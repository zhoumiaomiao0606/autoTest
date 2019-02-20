package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CreditStructTradeDetailLoanDO;

import java.util.List;

public interface CreditStructTradeDetailLoanDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CreditStructTradeDetailLoanDO record);

    int insertSelective(CreditStructTradeDetailLoanDO record);

    CreditStructTradeDetailLoanDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CreditStructTradeDetailLoanDO record);

    int updateByPrimaryKey(CreditStructTradeDetailLoanDO record);

    void deleteByCustomerId(Long customerId);

    List<CreditStructTradeDetailLoanDO> listByCustomerId(Long customerId);
}