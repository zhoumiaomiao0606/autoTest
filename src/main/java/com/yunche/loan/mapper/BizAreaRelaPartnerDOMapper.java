package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BizAreaRelaPartnerDO;
import com.yunche.loan.domain.entity.BizAreaRelaPartnerDOKey;

public interface BizAreaRelaPartnerDOMapper {
    int deleteByPrimaryKey(BizAreaRelaPartnerDOKey key);

    int insert(BizAreaRelaPartnerDO record);

    int insertSelective(BizAreaRelaPartnerDO record);

    BizAreaRelaPartnerDO selectByPrimaryKey(BizAreaRelaPartnerDOKey key);

    int updateByPrimaryKeySelective(BizAreaRelaPartnerDO record);

    int updateByPrimaryKey(BizAreaRelaPartnerDO record);
}