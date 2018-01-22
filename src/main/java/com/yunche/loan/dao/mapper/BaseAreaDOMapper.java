package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BaseAreaDO;
import com.yunche.loan.domain.QueryObj.BaseAreaQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseAreaDOMapper {
    int deleteByPrimaryKey(Long areaId);

    int insert(BaseAreaDO record);

    int insertSelective(BaseAreaDO record);

    BaseAreaDO selectByPrimaryKey(@Param("areaId") Long areaId, @Param("status") Byte status);

    int updateByPrimaryKeySelective(BaseAreaDO record);

    int updateByPrimaryKey(BaseAreaDO record);

    List<BaseAreaDO> query(BaseAreaQuery query);

    List<BaseAreaDO> getAll(@Param("status") Byte status);
}