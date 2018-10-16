package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessBridgeDO;
import org.apache.ibatis.annotations.Param;

public interface LoanProcessBridgeDOMapper {

    int deleteByPrimaryKey(Long orderId);

    int insert(LoanProcessBridgeDO record);

    int insertSelective(LoanProcessBridgeDO record);

    LoanProcessBridgeDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(LoanProcessBridgeDO record);

    int updateByPrimaryKey(LoanProcessBridgeDO record);

    LoanProcessBridgeDO selectByOrderId(@Param("orderId") Long orderId);
}