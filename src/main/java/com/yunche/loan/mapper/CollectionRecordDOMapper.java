package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CollectionRecordDO;
import com.yunche.loan.domain.param.RecordCollectionParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollectionRecordDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(CollectionRecordDO record);

    CollectionRecordDO selectByPrimaryKey(Long id);

    CollectionRecordDO selectNewest(Long orderId);

    List<CollectionRecordDO> selectNewestTotal(Long orderId);

    int updateByPrimaryKeySelective(CollectionRecordDO record);
}