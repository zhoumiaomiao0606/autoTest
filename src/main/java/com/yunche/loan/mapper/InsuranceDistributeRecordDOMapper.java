package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.InsuranceDistributeRecordDO;
import com.yunche.loan.domain.entity.InsuranceDistributeRecordDOKey;

import java.util.List;

public interface InsuranceDistributeRecordDOMapper {
    int deleteByPrimaryKey(InsuranceDistributeRecordDOKey key);

    int insert(InsuranceDistributeRecordDO record);

    int insertSelective(InsuranceDistributeRecordDO record);

    InsuranceDistributeRecordDO selectByPrimaryKey(InsuranceDistributeRecordDOKey key);

    int updateByPrimaryKeySelective(InsuranceDistributeRecordDO record);

    int updateByPrimaryKey(InsuranceDistributeRecordDO record);

    int insertBatch(List<InsuranceDistributeRecordDO> recordLists);
}