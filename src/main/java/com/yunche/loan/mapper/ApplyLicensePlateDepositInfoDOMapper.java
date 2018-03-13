package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ApplyLicensePlateDepositInfoDO;

public interface ApplyLicensePlateDepositInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(ApplyLicensePlateDepositInfoDO record);

    ApplyLicensePlateDepositInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ApplyLicensePlateDepositInfoDO record);
}