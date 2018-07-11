package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankCreditInfoDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankCreditInfoDOMapper {
    int deleteByPrimaryKey(String serialNo);

    int insertSelective(BankCreditInfoDO record);

    BankCreditInfoDO selectByPrimaryKey(String serialNo);

    int updateByPrimaryKeySelective(BankCreditInfoDO record);
}