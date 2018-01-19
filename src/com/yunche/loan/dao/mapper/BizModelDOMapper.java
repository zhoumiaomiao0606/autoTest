package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelDO;

public interface BizModelDOMapper {
    int deleteByPrimaryKey(Long bizId);

    int insert(BizModelDO record);

    int insertSelective(BizModelDO record);

    BizModelDO selectByPrimaryKey(Long bizId);

    int updateByPrimaryKeySelective(BizModelDO record);

    int updateByPrimaryKey(BizModelDO record);
}