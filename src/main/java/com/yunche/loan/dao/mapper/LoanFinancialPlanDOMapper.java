package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.LoanFinancialPlanDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanFinancialPlanDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanFinancialPlanDO record);

    int insertSelective(LoanFinancialPlanDO record);

    LoanFinancialPlanDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanFinancialPlanDO record);

    int updateByPrimaryKeyWithBLOBs(LoanFinancialPlanDO record);

    int updateByPrimaryKey(LoanFinancialPlanDO record);
}