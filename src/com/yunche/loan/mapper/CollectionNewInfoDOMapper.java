package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CollectionNewInfoDO;

public interface CollectionNewInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CollectionNewInfoDO record);

    int insertSelective(CollectionNewInfoDO record);

    CollectionNewInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CollectionNewInfoDO record);

    int updateByPrimaryKey(CollectionNewInfoDO record);
}