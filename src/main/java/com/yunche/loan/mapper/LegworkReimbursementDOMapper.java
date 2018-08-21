package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LegworkReimbursementDO;

import java.util.List;

public interface LegworkReimbursementDOMapper {

    List<LegworkReimbursementDO> list();

    LegworkReimbursementDO selectByPrimaryKey(Long id);

    int deleteByPrimaryKey(Long id);

    int insert(LegworkReimbursementDO record);

    int insertSelective(LegworkReimbursementDO record);

    int updateByPrimaryKeySelective(LegworkReimbursementDO record);

    int updateByPrimaryKey(LegworkReimbursementDO record);
}