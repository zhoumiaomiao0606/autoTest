package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCustRoleChangeHisDetailDO;

import java.util.List;

public interface LoanCustRoleChangeHisDetailDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanCustRoleChangeHisDetailDO record);

    int insertSelective(LoanCustRoleChangeHisDetailDO record);

    LoanCustRoleChangeHisDetailDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanCustRoleChangeHisDetailDO record);

    int updateByPrimaryKey(LoanCustRoleChangeHisDetailDO record);

    List<LoanCustRoleChangeHisDetailDO> listByRoleChangeHisId(Long roleChangeHisId);
}