package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LegworkReimbursementDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LegworkReimbursementDOMapper {
    int deleteByPrimaryKey(Long id);

     int insertSelective(LegworkReimbursementDO record);

    LegworkReimbursementDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LegworkReimbursementDO record);

}