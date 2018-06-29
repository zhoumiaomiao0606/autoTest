package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankInterfaceSerialDOMapper {
    int deleteByPrimaryKey(String serialNo);

    int insertSelective(BankInterfaceSerialDO record);

    BankInterfaceSerialDO selectByPrimaryKey(String serialNo);

    int updateByPrimaryKeySelective(BankInterfaceSerialDO record);

    BankInterfaceSerialDO selectByCustomerIdAndTransCode(Long customerId,String transCode);
}