package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.QueryObj.BizAreaQuery;
import com.yunche.loan.domain.dataObj.BizAreaRelaAreaDO;
import com.yunche.loan.domain.dataObj.BizAreaRelaAreaDOKey;
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
}