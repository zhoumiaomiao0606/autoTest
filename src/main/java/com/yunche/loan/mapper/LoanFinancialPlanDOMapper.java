package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanFinancialPlanDO;

import java.math.BigDecimal;

public interface LoanFinancialPlanDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanFinancialPlanDO record);

    int insertSelective(LoanFinancialPlanDO record);

    LoanFinancialPlanDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanFinancialPlanDO record);

    int updateByPrimaryKey(LoanFinancialPlanDO record);

    BigDecimal selectLoanAmount(Long orderId);


}