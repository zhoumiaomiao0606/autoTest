package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BizModelRelaFinancialProdDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BizModelRelaFinancialProdDOMapper {
    List<BizModelRelaFinancialProdDO> queryById(Long bizId);

    BizModelRelaFinancialProdDO queryByBizIdAndProdId(@Param("bizId") Long bizId, @Param("prodId") Long prodId);

    int deleteByPrimaryKey(Long bizId, Long prodId);

    int insert(BizModelRelaFinancialProdDO record);

    int update(BizModelRelaFinancialProdDO record);

    int insertSelective(BizModelRelaFinancialProdDO record);
}