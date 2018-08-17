package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.FeeRegisterDO;

public interface FeeRegisterDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FeeRegisterDO record);

    int insertSelective(FeeRegisterDO record);

    FeeRegisterDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FeeRegisterDO record);

    int updateByPrimaryKey(FeeRegisterDO record);
}