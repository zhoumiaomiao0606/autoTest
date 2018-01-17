package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelRelaAreaDOKey;

public interface BizModelRelaAreaDOMapper {
    int deleteByPrimaryKey(BizModelRelaAreaDOKey key);

    int insert(BizModelRelaAreaDOKey record);

    int insertSelective(BizModelRelaAreaDOKey record);
}