package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.InstProcessNodeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InstProcessNodeDOMapper {
    int deleteByPrimaryKey(Long nodeId);

    int insert(InstProcessNodeDO record);

    int insertSelective(InstProcessNodeDO record);

    InstProcessNodeDO selectByPrimaryKey(Long nodeId);

    int updateByPrimaryKeySelective(InstProcessNodeDO record);

    int updateByPrimaryKey(InstProcessNodeDO record);
}