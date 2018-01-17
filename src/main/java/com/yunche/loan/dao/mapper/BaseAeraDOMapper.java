package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BaseAreaDO;
import com.yunche.loan.domain.QueryObj.BaseAreaQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseAeraDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BaseAreaDO record);

    int insertSelective(BaseAreaDO record);

    BaseAreaDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BaseAreaDO record);

    int updateByPrimaryKey(BaseAreaDO record);

    List<BaseAreaDO> query(BaseAreaQuery query);
}