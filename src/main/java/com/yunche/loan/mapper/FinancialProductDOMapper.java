package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.FinancialQuery;
import com.yunche.loan.domain.entity.FinancialProductDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FinancialProductDOMapper {
    int deleteByPrimaryKey(Long prodId);

    int insert(FinancialProductDO record);

    int insertSelective(FinancialProductDO record);

    FinancialProductDO selectByPrimaryKey(Long prodId);

    List<FinancialProductDO> selectByCondition(FinancialQuery financialQuery);

    int updateByPrimaryKeySelective(FinancialProductDO record);

    int updateByPrimaryKey(FinancialProductDO record);

    Map selectProductInfoByOrderId(Long orderId);

    List<FinancialProductDO> listAll();
}