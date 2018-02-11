package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.ActRuTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface ActRuTaskDOMapper {
    int deleteByPrimaryKey(String id);

    int insert(ActRuTaskDO record);

    int insertSelective(ActRuTaskDO record);

    ActRuTaskDO selectByPrimaryKey(String id);

    List<ActRuTaskDO> selectByProcInstId(String procInstId);

    int updateByPrimaryKeySelective(ActRuTaskDO record);

    int updateByPrimaryKey(ActRuTaskDO record);
}