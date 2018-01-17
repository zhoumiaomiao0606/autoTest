package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelRelaPartnersDOKey;

public interface BizModelRelaPartnersDOMapper {
    int deleteByPrimaryKey(BizModelRelaPartnersDOKey key);

    int insert(BizModelRelaPartnersDOKey record);

    int insertSelective(BizModelRelaPartnersDOKey record);
}