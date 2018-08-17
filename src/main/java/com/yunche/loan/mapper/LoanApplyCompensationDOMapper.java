package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import com.yunche.loan.domain.entity.LoanApplyCompensationDOKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanApplyCompensationDOMapper {
    int deleteByPrimaryKey(LoanApplyCompensationDOKey key);

    int insert(LoanApplyCompensationDO record);

    int insertSelective(LoanApplyCompensationDO record);

    LoanApplyCompensationDO selectByPrimaryKey(LoanApplyCompensationDOKey key);

    int updateByPrimaryKeySelective(LoanApplyCompensationDO record);

    int updateByPrimaryKey(LoanApplyCompensationDO record);

    List<LoanApplyCompensationDO> selectByOrderId(@Param("orderId")Long orderId);
}