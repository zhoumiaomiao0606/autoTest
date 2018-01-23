package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.QueryObj.BizModelQuery;
import com.yunche.loan.domain.dataObj.BizModelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizModelDOMapper {
    int deleteByPrimaryKey(Long bizId);

    long insert(BizModelDO record);

    int insertSelective(BizModelDO record);

    BizModelDO selectByPrimaryKey(Long bizId);

    int updateByPrimaryKeySelective(BizModelDO record);

    int updateByPrimaryKey(BizModelDO record);

    List<BizModelDO> selectByCondition(BizModelQuery bizModelQuery);
}