package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanFinancialPlanDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(LoanFinancialPlanDO record);

    LoanFinancialPlanDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanFinancialPlanDO record);

    int updateByPrimaryKeyWithBLOBs(LoanFinancialPlanDO record);

}