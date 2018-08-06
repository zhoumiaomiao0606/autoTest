package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankCarLicenseLocationDO;

import java.util.List;

public interface BankCarLicenseLocationDOMapper {
    List<BankCarLicenseLocationDO> listByBankId(Long bankId);

    void deleteByBankId(Long bankId);

    int deleteByPrimaryKey(Long id);

    int insertSelective(BankCarLicenseLocationDO record);

    BankCarLicenseLocationDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankCarLicenseLocationDO record);
}