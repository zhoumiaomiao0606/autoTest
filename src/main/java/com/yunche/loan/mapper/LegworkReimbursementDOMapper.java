package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LegworkReimbursementDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LegworkReimbursementDOMapper {
    List<LegworkReimbursementDO> list();

    int deleteByPrimaryKey(Long id);

     int insertSelective(LegworkReimbursementDO record);

    LegworkReimbursementDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LegworkReimbursementDO record);

}