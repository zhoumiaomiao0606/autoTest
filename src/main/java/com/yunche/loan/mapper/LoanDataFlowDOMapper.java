package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanDataFlowDO;

public interface LoanDataFlowDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanDataFlowDO record);

    int insertSelective(LoanDataFlowDO record);

    LoanDataFlowDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanDataFlowDO record);

    int updateByPrimaryKey(LoanDataFlowDO record);
}