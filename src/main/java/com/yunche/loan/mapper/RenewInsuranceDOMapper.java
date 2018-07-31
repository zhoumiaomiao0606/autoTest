package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.RenewInsuranceDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RenewInsuranceDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RenewInsuranceDO record);

    int insertSelective(RenewInsuranceDO record);

    RenewInsuranceDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RenewInsuranceDO record);

    int updateByPrimaryKey(RenewInsuranceDO record);
}