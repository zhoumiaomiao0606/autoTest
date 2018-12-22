package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankCodeDO;

import java.util.List;

public interface BankCodeDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BankCodeDO record);

    int insertSelective(BankCodeDO record);

    BankCodeDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BankCodeDO record);

    int updateByPrimaryKey(BankCodeDO record);

    List<BankCodeDO> selectByBankName(String bankName);
}