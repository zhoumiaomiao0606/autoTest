package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CollectionRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CollectionRecordDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(CollectionRecordDO record);

    CollectionRecordDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CollectionRecordDO record);
}