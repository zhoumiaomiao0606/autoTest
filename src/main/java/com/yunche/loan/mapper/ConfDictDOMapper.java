package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ConfDictDO;

import java.util.List;

public interface ConfDictDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ConfDictDO record);

    int insertSelective(ConfDictDO record);

    ConfDictDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ConfDictDO record);

    int updateByPrimaryKeyWithBLOBs(ConfDictDO record);

    int updateByPrimaryKey(ConfDictDO record);

    List<ConfDictDO> getAll();
}