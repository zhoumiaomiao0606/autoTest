package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.InsuranceInfoDO;

public interface InsuranceInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(InsuranceInfoDO record);

    InsuranceInfoDO selectByPrimaryKey(Long id);

    InsuranceInfoDO selectLastUpdateRecordByOrderId(Long orderId);

    int updateByPrimaryKeySelective(InsuranceInfoDO record);
}