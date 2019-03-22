package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.YuncheBoardDO;
import com.yunche.loan.domain.param.YuncheBoardParam;

import java.util.List;

public interface YuncheBoardDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(YuncheBoardDO record);

    int insertSelective(YuncheBoardDO record);

    YuncheBoardDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(YuncheBoardDO record);

    int updateByPrimaryKeyWithBLOBs(YuncheBoardDO record);

    int updateByPrimaryKey(YuncheBoardDO record);

    List<YuncheBoardDO> selectBoards(YuncheBoardParam yuncheBoardParam);
}