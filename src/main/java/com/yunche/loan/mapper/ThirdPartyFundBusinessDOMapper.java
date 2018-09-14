package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ThirdPartyFundBusinessDO;

import java.util.List;

public interface ThirdPartyFundBusinessDOMapper {

    int deleteByPrimaryKey(Long bridgeProcecssId);

    int insert(ThirdPartyFundBusinessDO record);

    int insertSelective(ThirdPartyFundBusinessDO record);

    ThirdPartyFundBusinessDO selectByPrimaryKey(Long bridgeProcecssId);

    int updateByPrimaryKeySelective(ThirdPartyFundBusinessDO record);

    int updateByPrimaryKey(ThirdPartyFundBusinessDO record);

    int batchInsert(List<ThirdPartyFundBusinessDO> thirdPartyFundBusinessDOS);
}