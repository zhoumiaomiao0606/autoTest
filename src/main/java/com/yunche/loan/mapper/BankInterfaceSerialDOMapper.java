package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BankInterfaceSerialDOMapper {
    int deleteByPrimaryKey(String serialNo);

    int insertSelective(BankInterfaceSerialDO record);

    BankInterfaceSerialDO selectByPrimaryKey(String serialNo);

    int updateByPrimaryKeySelective(BankInterfaceSerialDO record);

    BankInterfaceSerialDO selectByCustomerIdAndTransCode(@Param("customerId") Long customerId, @Param("transCode") String transCode);
}