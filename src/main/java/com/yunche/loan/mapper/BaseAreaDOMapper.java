package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.query.BaseAreaQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseAreaDOMapper {
    int deleteByPrimaryKey(Long areaId);

    int insert(BaseAreaDO record);

    int insertSelective(BaseAreaDO record);

    BaseAreaDO selectByPrimaryKey(@Param("areaId") Long areaId, @Param("status") Byte status);

    List<BaseAreaDO> selectByIdList(@Param("areaIdList")List<Long> areaIdList, @Param("status") Byte status);

    int updateByPrimaryKeySelective(BaseAreaDO record);

    int updateByPrimaryKey(BaseAreaDO record);

    List<BaseAreaDO> query(BaseAreaQuery query);

    List<BaseAreaDO> getAll(@Param("status") Byte status);
}