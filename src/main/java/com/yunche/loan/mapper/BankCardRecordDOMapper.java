package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankCardRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankCardRecordDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BankCardRecordDO record);

    int insertSelective(BankCardRecordDO record);

    BankCardRecordDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BankCardRecordDO record);

    int updateByPrimaryKey(BankCardRecordDO record);

    BankCardRecordDO selectByOrderId(Long orderId);

    int updateByOrderId(BankCardRecordDO record);
}