package com.yunche.loan.mapper;

import com.yunche.loan.obj.BaseAeraDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BaseAeraDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BaseAeraDO record);

    int insertSelective(BaseAeraDO record);

    BaseAeraDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BaseAeraDO record);

    int updateByPrimaryKey(BaseAeraDO record);
}