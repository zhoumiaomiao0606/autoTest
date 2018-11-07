package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCarInfoDO;
import com.yunche.loan.domain.entity.LoanProcessLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoanCarInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanCarInfoDO record);

    int insertSelective(LoanCarInfoDO record);

    LoanCarInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanCarInfoDO record);

    int updateByPrimaryKey(LoanCarInfoDO record);

    @Select("SELECT `car_key`  FROM `loan_car_info` WHERE `id` = " +
            "(SELECT  `loan_car_info_id` from `loan_order` WHERE `id`  = #{orderId})")
    Byte getCarKeyByOrderId(Long orderId);

    LoanProcessLogDO selectNeedCollectKey(Long orderId);

    String selectFullNameById(@Param("loanCarId")Long loanCarId);
}