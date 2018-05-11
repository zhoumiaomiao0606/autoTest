package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankRepayRecordDO;
import com.yunche.loan.domain.entity.BankRepayRecordDOKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankRepayRecordDOMapper {
    int deleteByPrimaryKey(BankRepayRecordDOKey key);

    int insert(BankRepayRecordDO record);

    int insertSelective(BankRepayRecordDO record);

    BankRepayRecordDO selectByPrimaryKey(BankRepayRecordDOKey key);

    int updateByPrimaryKeySelective(BankRepayRecordDO record);

    int updateByPrimaryKey(BankRepayRecordDO record);
}