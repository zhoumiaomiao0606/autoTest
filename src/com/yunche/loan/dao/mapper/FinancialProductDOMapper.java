package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.FinancialProductDO;

public interface FinancialProductDOMapper {
    int deleteByPrimaryKey(Long prodId);

    int insert(FinancialProductDO record);

    int insertSelective(FinancialProductDO record);

    FinancialProductDO selectByPrimaryKey(Long prodId);

    int updateByPrimaryKeySelective(FinancialProductDO record);

    int updateByPrimaryKey(FinancialProductDO record);
}