package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessLogDO;
import com.yunche.loan.domain.entity.LoanRejectLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoanRejectLogDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanRejectLogDO record);

    int insertSelective(LoanRejectLogDO record);

    LoanRejectLogDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanRejectLogDO record);

    int updateByPrimaryKey(LoanRejectLogDO record);

    LoanRejectLogDO lastByOrderIdAndTaskDefinitionKey(@Param("orderId") Long orderId, @Param("rejectToTask") String rejectToTask);
}