package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelRelaAreaPartnersDO;
import com.yunche.loan.domain.dataObj.BizModelRelaFinancialProdDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizModelRelaFinancialProdDOMapper {
    List<BizModelRelaFinancialProdDO> queryById(Long bizId);

    int deleteByPrimaryKey(Long bizId, Long prodId);

    int insert(BizModelRelaFinancialProdDO record);

    int update(BizModelRelaFinancialProdDO record);

    int insertSelective(BizModelRelaFinancialProdDO record);
}