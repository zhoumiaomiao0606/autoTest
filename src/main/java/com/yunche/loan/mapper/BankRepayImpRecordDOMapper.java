package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankRepayImpRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankRepayImpRecordDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BankRepayImpRecordDO record);

    int insertSelective(BankRepayImpRecordDO record);

    BankRepayImpRecordDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankRepayImpRecordDO record);

    int updateByPrimaryKey(BankRepayImpRecordDO record);
}