package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanTelephoneVerifyDO;

public interface LoanTelephoneVerifyDOMapper {

    int deleteByPrimaryKey(Long orderId);

    int insert(LoanTelephoneVerifyDO record);

    int insertSelective(LoanTelephoneVerifyDO record);

    LoanTelephoneVerifyDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(LoanTelephoneVerifyDO record);

    int updateByPrimaryKey(LoanTelephoneVerifyDO record);
}