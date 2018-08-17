package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LitigationDO;

public interface LitigationDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LitigationDO record);

    int insertSelective(LitigationDO record);

    LitigationDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LitigationDO record);

    int updateByPrimaryKey(LitigationDO record);
}