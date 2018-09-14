package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface LoanApplyCompensationDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanApplyCompensationDO record);

    int insertSelective(LoanApplyCompensationDO record);

    LoanApplyCompensationDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanApplyCompensationDO record);

    int updateByPrimaryKey(LoanApplyCompensationDO record);

    List<LoanApplyCompensationDO> selectByOrderId(@Param("orderId") Long orderId);


    LoanApplyCompensationDO selectByOrderIdAndDate(@Param("orderId") Long orderId,
                                                   @Param("applyCompensationDate") Date applyCompensationDate);

    LoanApplyCompensationDO selectLastByOrderId(@Param("orderId") Long orderId);

    LoanApplyCompensationDO selectByOrderIdAndBankRepayImpRecordId(@Param("orderId") Long orderId,
                                                                   @Param("bank_repay_imp_record_id") Long bank_repay_imp_record_id);
}