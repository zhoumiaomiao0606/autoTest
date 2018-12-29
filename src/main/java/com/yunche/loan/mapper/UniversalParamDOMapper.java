package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.UniversalParamDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UniversalParamDOMapper {
    int deleteByPrimaryKey(String paramId);

    int insert(UniversalParamDO record);

    int insertSelective(UniversalParamDO record);

    UniversalParamDO selectByPrimaryKey(String paramId);

    int updateByPrimaryKeySelective(UniversalParamDO record);

    int updateByPrimaryKey(UniversalParamDO record);

    List<UniversalParamDO> allParam();
}