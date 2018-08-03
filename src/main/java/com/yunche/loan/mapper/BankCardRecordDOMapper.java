package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankCardRecordDO;

public interface BankCardRecordDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(BankCardRecordDO record);

    int insertSelective(BankCardRecordDO record);

    BankCardRecordDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankCardRecordDO record);

    int updateByPrimaryKey(BankCardRecordDO record);

    int updateByOrderId(BankCardRecordDO record);

    BankCardRecordDO selectByOrderId(Long orderId);
}