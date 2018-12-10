package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.FinancialOperationExceptionDO;
import com.yunche.loan.domain.param.FinancialExceptionOperationParam;
import com.yunche.loan.domain.vo.FinancialExceptionOperationVO;

import java.util.List;

public interface FinancialOperationExceptionDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FinancialOperationExceptionDO record);

    int insertSelective(FinancialOperationExceptionDO record);

    FinancialOperationExceptionDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FinancialOperationExceptionDO record);

    int updateByPrimaryKey(FinancialOperationExceptionDO record);

    List<FinancialExceptionOperationVO> list(FinancialExceptionOperationParam financialExceptionOperationParam);
}