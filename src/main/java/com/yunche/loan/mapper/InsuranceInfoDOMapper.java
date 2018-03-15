package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.InsuranceInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InsuranceInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(InsuranceInfoDO record);

    InsuranceInfoDO selectByPrimaryKey(Long id);

    InsuranceInfoDO selectByInsuranceYear(@Param("orderId") Long orderId,@Param("insuranceYear") Byte insuranceYear);

    int updateByPrimaryKeySelective(InsuranceInfoDO record);
}