package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelRelaFinancialProdDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizModelRelaFinancialProdDOMapper {
    int deleteByPrimaryKey(BizModelRelaFinancialProdDO key);

    int insert(BizModelRelaFinancialProdDO record);

    int insertSelective(BizModelRelaFinancialProdDO record);
}