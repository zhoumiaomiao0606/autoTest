package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCustRoleChangeHisDO;

public interface LoanCustRoleChangeHisDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanCustRoleChangeHisDO record);

    int insertSelective(LoanCustRoleChangeHisDO record);

    LoanCustRoleChangeHisDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanCustRoleChangeHisDO record);

    int updateByPrimaryKey(LoanCustRoleChangeHisDO record);
}