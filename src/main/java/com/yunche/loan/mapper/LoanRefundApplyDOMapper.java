package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanRefundApplyDO;

public interface LoanRefundApplyDOMapper {

    int deleteByPrimaryKey(Long id);

    int insertSelective(LoanRefundApplyDO record);

    LoanRefundApplyDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanRefundApplyDO record);

    LoanRefundApplyDO lastByOrderId(Long orderId);
}