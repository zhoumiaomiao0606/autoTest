package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CashierAccountConfDO;

public interface CashierAccountConfDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CashierAccountConfDO record);

    int insertSelective(CashierAccountConfDO record);

    CashierAccountConfDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CashierAccountConfDO record);

    int updateByPrimaryKey(CashierAccountConfDO record);
}