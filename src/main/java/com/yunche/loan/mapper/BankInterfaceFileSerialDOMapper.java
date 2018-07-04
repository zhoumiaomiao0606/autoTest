package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankInterfaceFileSerialDO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface BankInterfaceFileSerialDOMapper {
    int deleteByPrimaryKey(String id);

    int insertSelective(BankInterfaceFileSerialDO record);

    BankInterfaceFileSerialDO selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BankInterfaceFileSerialDO record);
}