package com.yunche.loan.mapper.configure.info.address;

import com.yunche.loan.obj.configure.info.address.BaseAreaDO;
import com.yunche.loan.query.configure.info.address.BaseAreaQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseAeraDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BaseAreaDO record);

    int insertSelective(BaseAreaDO record);

    BaseAreaDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BaseAreaDO record);

    int updateByPrimaryKey(BaseAreaDO record);

    List<BaseAreaDO> query(BaseAreaQuery query);
}