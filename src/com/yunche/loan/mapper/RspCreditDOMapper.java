package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.RspCreditDO;

public interface RspCreditDOMapper {
    int insert(RspCreditDO record);

    int insertSelective(RspCreditDO record);
}