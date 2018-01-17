package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelRelaFinancialProdDOKey;

public interface BizModelRelaFinancialProdDOMapper {
    int deleteByPrimaryKey(BizModelRelaFinancialProdDOKey key);

    int insert(BizModelRelaFinancialProdDOKey record);

    int insertSelective(BizModelRelaFinancialProdDOKey record);
}