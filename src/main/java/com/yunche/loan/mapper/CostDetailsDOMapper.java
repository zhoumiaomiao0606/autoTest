package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CostDetailsDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CostDetailsDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(CostDetailsDO record);

    CostDetailsDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CostDetailsDO record);

}