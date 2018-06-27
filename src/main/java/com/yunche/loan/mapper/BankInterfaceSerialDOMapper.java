package com.yunche.loan.mapper;

public interface BankInterfaceSerialDOMapper {
    int deleteByPrimaryKey(String serialNo);

    int insertSelective(BankInterfaceSerialDO record);

    BankInterfaceSerialDO selectByPrimaryKey(String serialNo);

    int updateByPrimaryKeySelective(BankInterfaceSerialDO record);
}