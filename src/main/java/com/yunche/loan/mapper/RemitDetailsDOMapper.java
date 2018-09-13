package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.RemitDetailsDO;

public interface RemitDetailsDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(RemitDetailsDO record);

    int insertSelective(RemitDetailsDO record);

    RemitDetailsDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RemitDetailsDO record);

    int updateByPrimaryKey(RemitDetailsDO record);
}