package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CollectionNewInfoDO;
import com.yunche.loan.domain.entity.CollectionNewInfoDOKey;

public interface CollectionNewInfoDOMapper {
    int deleteByPrimaryKey(CollectionNewInfoDOKey key);

    int insert(CollectionNewInfoDO record);

    int insertSelective(CollectionNewInfoDO record);

    CollectionNewInfoDO selectByPrimaryKey(CollectionNewInfoDOKey key);

    int updateByPrimaryKeySelective(CollectionNewInfoDO record);

    int updateByPrimaryKey(CollectionNewInfoDO record);
}