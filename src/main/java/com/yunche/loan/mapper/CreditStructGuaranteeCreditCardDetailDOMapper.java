package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CreditStructGuaranteeCreditCardDetailDO;

import java.util.List;

public interface CreditStructGuaranteeCreditCardDetailDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CreditStructGuaranteeCreditCardDetailDO record);

    int insertSelective(CreditStructGuaranteeCreditCardDetailDO record);

    CreditStructGuaranteeCreditCardDetailDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CreditStructGuaranteeCreditCardDetailDO record);

    int updateByPrimaryKey(CreditStructGuaranteeCreditCardDetailDO record);

    void deleteByCustomerId(Long customerId);

    List<CreditStructGuaranteeCreditCardDetailDO> listByCustomerId(Long customerId);
}