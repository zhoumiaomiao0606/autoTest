package com.yunche.loan.dao;

import com.yunche.loan.domain.query.BizAreaQuery;
import com.yunche.loan.domain.entity.BizAreaRelaAreaDO;
import com.yunche.loan.domain.entity.BizAreaRelaAreaDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizAreaRelaAreaDOMapper {
    int deleteByPrimaryKey(BizAreaRelaAreaDOKey key);

    int insert(BizAreaRelaAreaDO record);

    int insertSelective(BizAreaRelaAreaDO record);

    BizAreaRelaAreaDO selectByPrimaryKey(BizAreaRelaAreaDOKey key);

    int updateByPrimaryKeySelective(BizAreaRelaAreaDO record);

    int updateByPrimaryKey(BizAreaRelaAreaDO record);

    int count(BizAreaQuery query);

    List<BizAreaRelaAreaDO> query(BizAreaQuery query);

    int batchInsert(List<BizAreaRelaAreaDO> bizAreaRelaAreaDOS);

    List<Long> getAreaIdListByBizAreaId(Long bizAreaId);
}