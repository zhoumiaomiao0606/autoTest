package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.SerialNoDO;
import com.yunche.loan.domain.entity.SerialNoDOKey;

public interface SerialNoDOMapper {
    int deleteByPrimaryKey(SerialNoDOKey key);

    int insert(SerialNoDO record);

    int insertSelective(SerialNoDO record);

    SerialNoDO selectByPrimaryKey(SerialNoDOKey key);

    int updateByPrimaryKeySelective(SerialNoDO record);

    int updateByPrimaryKey(SerialNoDO record);
}