package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanRepayPlanDO;
import com.yunche.loan.domain.entity.LoanRepayPlanDOKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper

public interface LoanRepayPlanDOMapper {
    int deleteByPrimaryKey(LoanRepayPlanDOKey key);

    int insert(LoanRepayPlanDO record);

    int insertSelective(LoanRepayPlanDO record);

    LoanRepayPlanDO selectByPrimaryKey(LoanRepayPlanDOKey key);

    int updateByPrimaryKeySelective(LoanRepayPlanDO record);

    int updateByPrimaryKey(LoanRepayPlanDO record);
}