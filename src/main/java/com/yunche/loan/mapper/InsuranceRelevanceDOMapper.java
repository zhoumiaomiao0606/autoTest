package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.InsuranceRelevanceDO;

public interface InsuranceRelevanceDOMapper {
    int deleteByPrimaryKey(Long id);

    int deleteByInsuranceInfoId(Long insuranceInfoId);

    int insertSelective(InsuranceRelevanceDO record);

    InsuranceRelevanceDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InsuranceRelevanceDO record);
}