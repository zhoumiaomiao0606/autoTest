package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelRelaFinancialProdDO;

public interface BizModelRelaFinancialProdDOMapper {
    int deleteByPrimaryKey(BizModelRelaFinancialProdDO key);

    int insert(BizModelRelaFinancialProdDO record);

    int insertSelective(BizModelRelaFinancialProdDO record);
}