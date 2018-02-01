package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.queryObj.FinancialQuery;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FinancialProductDOMapper {
    int deleteByPrimaryKey(Long prodId);

    int insert(FinancialProductDO record);

    int insertSelective(FinancialProductDO record);

    FinancialProductDO selectByPrimaryKey(Long prodId);

    List<FinancialProductDO> selectByCondition(FinancialQuery financialQuery);

    int updateByPrimaryKeySelective(FinancialProductDO record);

    int updateByPrimaryKey(FinancialProductDO record);
}