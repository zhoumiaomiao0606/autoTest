package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CreditStructTradeDetailDO;

import java.util.List;

public interface CreditStructTradeDetailDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CreditStructTradeDetailDO record);

    int insertSelective(CreditStructTradeDetailDO record);

    CreditStructTradeDetailDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CreditStructTradeDetailDO record);

    int updateByPrimaryKey(CreditStructTradeDetailDO record);

    void deleteByCustomerId(Long customerId);

    List<CreditStructTradeDetailDO> listByCustomerId(Long customerId);
}