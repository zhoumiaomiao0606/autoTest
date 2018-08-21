package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface LoanApplyCompensationDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanApplyCompensationDO record);

    int insertSelective(LoanApplyCompensationDO record);

    LoanApplyCompensationDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanApplyCompensationDO record);

    int updateByPrimaryKey(LoanApplyCompensationDO record);

    List<LoanApplyCompensationDO> selectByOrderId(@Param("orderId")Long orderId);


    LoanApplyCompensationDO selectByOrderIdAndDate(@Param("orderId")Long orderId,@Param("applyCompensationDate") Date applyCompensationDate);

}