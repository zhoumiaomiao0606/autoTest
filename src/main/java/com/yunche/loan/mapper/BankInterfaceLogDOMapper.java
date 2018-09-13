package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankInterfaceLogDO;
import com.yunche.loan.domain.entity.BankInterfaceLogDOKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankInterfaceLogDOMapper {
    int deleteByPrimaryKey(BankInterfaceLogDOKey key);

    int insert(BankInterfaceLogDO record);

    int insertSelective(BankInterfaceLogDO record);

    BankInterfaceLogDO selectByPrimaryKey(BankInterfaceLogDOKey key);

    int updateByPrimaryKeySelective(BankInterfaceLogDO record);

    int updateByPrimaryKey(BankInterfaceLogDO record);
}