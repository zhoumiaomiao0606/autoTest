package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanFinancialPlanDO;

public interface LoanFinancialPlanDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanFinancialPlanDO record);

    int insertSelective(LoanFinancialPlanDO record);

    LoanFinancialPlanDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanFinancialPlanDO record);

    int updateByPrimaryKey(LoanFinancialPlanDO record);
}