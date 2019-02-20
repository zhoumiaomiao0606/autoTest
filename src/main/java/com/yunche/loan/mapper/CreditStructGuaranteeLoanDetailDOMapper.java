package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CreditStructGuaranteeLoanDetailDO;

import java.util.List;

public interface CreditStructGuaranteeLoanDetailDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CreditStructGuaranteeLoanDetailDO record);

    int insertSelective(CreditStructGuaranteeLoanDetailDO record);

    CreditStructGuaranteeLoanDetailDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CreditStructGuaranteeLoanDetailDO record);

    int updateByPrimaryKey(CreditStructGuaranteeLoanDetailDO record);

    void deleteByCustomerId(Long customerId);

    List<CreditStructGuaranteeLoanDetailDO> listByCustomerId(Long customerId);
}