package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizModelDOMapper {
    int deleteByPrimaryKey(Long bizId);

    long insert(BizModelDO record);

    int insertSelective(BizModelDO record);

    BizModelDO selectByPrimaryKey(Long bizId);

    int updateByPrimaryKeySelective(BizModelDO record);

    int updateByPrimaryKey(BizModelDO record);
}