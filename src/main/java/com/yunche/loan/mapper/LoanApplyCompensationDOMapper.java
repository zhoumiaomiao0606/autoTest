package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import com.yunche.loan.domain.entity.LoanApplyCompensationDOKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanApplyCompensationDOMapper {
    int deleteByPrimaryKey(LoanApplyCompensationDOKey key);

    int insert(LoanApplyCompensationDO record);

    int insertSelective(LoanApplyCompensationDO record);

    LoanApplyCompensationDO selectByPrimaryKey(LoanApplyCompensationDOKey key);

    int updateByPrimaryKeySelective(LoanApplyCompensationDO record);

    int updateByPrimaryKey(LoanApplyCompensationDO record);
}