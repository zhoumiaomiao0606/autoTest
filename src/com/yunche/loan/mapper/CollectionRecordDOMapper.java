package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CollectionRecordDO;

public interface CollectionRecordDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CollectionRecordDO record);

    int insertSelective(CollectionRecordDO record);

    CollectionRecordDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CollectionRecordDO record);

    int updateByPrimaryKey(CollectionRecordDO record);
}