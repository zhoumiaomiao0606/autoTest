package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.InsuranceRelevanceDO;

public interface InsuranceRelevanceDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(InsuranceRelevanceDO record);

    InsuranceRelevanceDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InsuranceRelevanceDO record);
}