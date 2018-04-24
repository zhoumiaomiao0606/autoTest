package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanFinancialPlanTempDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanFinancialPlanTempDOMapper {
    int deleteByPrimaryKey(Long order_id);

    int insertSelective(LoanFinancialPlanTempDO record);

    LoanFinancialPlanTempDO selectByPrimaryKey(Long order_id);

    int updateByPrimaryKeySelective(LoanFinancialPlanTempDO record);
}
