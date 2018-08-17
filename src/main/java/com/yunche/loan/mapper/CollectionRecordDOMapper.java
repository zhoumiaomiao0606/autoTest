package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CollectionRecordDO;
import com.yunche.loan.domain.param.RecordCollectionParam;
import com.yunche.loan.domain.vo.CollectionRecordVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollectionRecordDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(CollectionRecordDO record);

    CollectionRecordDO selectByPrimaryKey(Long id);

    CollectionRecordVO selectNewest(Long orderId);

    List<CollectionRecordDO> selectNewestTotal(Long orderId);

    int updateByPrimaryKeySelective(CollectionRecordDO record);
}