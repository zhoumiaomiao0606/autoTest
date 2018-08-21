package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.FeeRegisterDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeeRegisterDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FeeRegisterDO record);

    int insertSelective(FeeRegisterDO record);

    FeeRegisterDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FeeRegisterDO record);

    int updateByPrimaryKey(FeeRegisterDO record);

    FeeRegisterDO selectByOrderId(@Param("orderid")Long orderid,@Param("bankRepayImpRecordId")Long bankRepayImpRecordId);
}