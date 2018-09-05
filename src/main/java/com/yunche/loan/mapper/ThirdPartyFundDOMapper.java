package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ThirdPartyFundDO;

import java.util.List;

public interface ThirdPartyFundDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ThirdPartyFundDO record);

    int insertSelective(ThirdPartyFundDO record);

    ThirdPartyFundDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ThirdPartyFundDO record);

    int updateByPrimaryKey(ThirdPartyFundDO record);

    List<ThirdPartyFundDO> list();
}