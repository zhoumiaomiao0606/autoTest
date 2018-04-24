package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanFinancialPlanTempHisDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanFinancialPlanTempHisDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(LoanFinancialPlanTempHisDO record);

    LoanFinancialPlanTempHisDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanFinancialPlanTempHisDO record);
}