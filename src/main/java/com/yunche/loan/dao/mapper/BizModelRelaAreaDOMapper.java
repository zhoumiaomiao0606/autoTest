package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelRelaAreaDO;

public interface BizModelRelaAreaDOMapper {
    int deleteByPrimaryKey(BizModelRelaAreaDO key);

    int insert(BizModelRelaAreaDO record);

    int insertSelective(BizModelRelaAreaDO record);
}