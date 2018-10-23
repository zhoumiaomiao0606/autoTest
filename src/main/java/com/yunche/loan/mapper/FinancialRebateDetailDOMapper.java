package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.FinancialRebateDetailDO;
import com.yunche.loan.domain.entity.FinancialRebateDetailDOKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FinancialRebateDetailDOMapper {
    int deleteByPrimaryKey(FinancialRebateDetailDOKey key);

    int insert(FinancialRebateDetailDO record);

    int insertSelective(FinancialRebateDetailDO record);

    FinancialRebateDetailDO selectByPrimaryKey(FinancialRebateDetailDOKey key);

    int updateByPrimaryKeySelective(FinancialRebateDetailDO record);

    int updateByPrimaryKey(FinancialRebateDetailDO record);
}