package com.yunche.loan.mapper;

import com.yunche.loan.config.anno.FunctionTime;
import com.yunche.loan.domain.query.FinancialQuery;
import com.yunche.loan.domain.entity.FinancialProductDO;

import java.util.List;
import java.util.Map;

public interface FinancialProductDOMapper {

    int deleteByPrimaryKey(Long prodId);

    int insert(FinancialProductDO record);

    int insertSelective(FinancialProductDO record);

    FinancialProductDO selectByPrimaryKey(Long prodId);

    List<FinancialProductDO> selectByCondition(FinancialQuery financialQuery);

    int updateByPrimaryKeySelective(FinancialProductDO record);

    int updateByPrimaryKey(FinancialProductDO record);


    @FunctionTime
    Map selectProductInfoByOrderId(Long orderId);

    Map selectProductInfoByOrderIdNew(Long orderId);


    List<FinancialProductDO> listAll();
}