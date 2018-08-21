package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ForceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ForceDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ForceDO record);

    int insertSelective(ForceDO record);

    ForceDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ForceDO record);

    int updateByPrimaryKey(ForceDO record);

    ForceDO selectByOrderId(@Param("orderid")Long orderid,@Param("bankRepayImpRecordId")Long bankRepayImpRecordId);
}