package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessBridgeDO;

public interface LoanProcessBridgeDOMapper {

    int deleteByPrimaryKey(Long orderId);

    int insert(LoanProcessBridgeDO record);

    int insertSelective(LoanProcessBridgeDO record);

    LoanProcessBridgeDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(LoanProcessBridgeDO record);

    int updateByPrimaryKey(LoanProcessBridgeDO record);
}