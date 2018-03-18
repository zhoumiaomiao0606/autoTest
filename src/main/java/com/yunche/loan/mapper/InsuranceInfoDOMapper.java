package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.InsuranceInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InsuranceInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(InsuranceInfoDO record);

    InsuranceInfoDO selectByPrimaryKey(Long id);

    InsuranceInfoDO selectByInsuranceYear(@Param("orderId") Long orderId, @Param("insuranceYear") Byte insuranceYear);

    List<InsuranceInfoDO> listByOrderId(@Param("orderId") Long orderId);

    int updateByPrimaryKeySelective(InsuranceInfoDO record);
}