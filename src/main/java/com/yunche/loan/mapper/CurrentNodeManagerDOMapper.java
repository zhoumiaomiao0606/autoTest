package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CurrentNodeManagerDO;

public interface CurrentNodeManagerDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(CurrentNodeManagerDO record);

    int insertSelective(CurrentNodeManagerDO record);

    CurrentNodeManagerDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(CurrentNodeManagerDO record);

    int updateByPrimaryKey(CurrentNodeManagerDO record);
}