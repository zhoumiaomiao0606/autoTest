package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VisitDoorDO;

public interface VisitDoorDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(VisitDoorDO record);

    int insertSelective(VisitDoorDO record);

    VisitDoorDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VisitDoorDO record);

    int updateByPrimaryKey(VisitDoorDO record);
}