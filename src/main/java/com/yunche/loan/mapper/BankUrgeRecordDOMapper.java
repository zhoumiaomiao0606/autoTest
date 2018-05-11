package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankUrgeRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankUrgeRecordDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(BankUrgeRecordDO record);

    int insertSelective(BankUrgeRecordDO record);

    BankUrgeRecordDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(BankUrgeRecordDO record);

    int updateByPrimaryKey(BankUrgeRecordDO record);
}