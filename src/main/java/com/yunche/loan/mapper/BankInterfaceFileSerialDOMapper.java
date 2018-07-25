package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankInterfaceFileSerialDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankInterfaceFileSerialDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BankInterfaceFileSerialDO record);

    int insertSelective(BankInterfaceFileSerialDO record);

    BankInterfaceFileSerialDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankInterfaceFileSerialDO record);

    int updateByPrimaryKey(BankInterfaceFileSerialDO record);
}