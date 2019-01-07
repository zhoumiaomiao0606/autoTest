package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ConfVideoFaceBankDO;

public interface ConfVideoFaceBankDOMapper {

    int deleteByPrimaryKey(Long bankId);

    int insert(ConfVideoFaceBankDO record);

    int insertSelective(ConfVideoFaceBankDO record);

    ConfVideoFaceBankDO selectByPrimaryKey(Long bankId);

    int updateByPrimaryKeySelective(ConfVideoFaceBankDO record);

    int updateByPrimaryKey(ConfVideoFaceBankDO record);
}