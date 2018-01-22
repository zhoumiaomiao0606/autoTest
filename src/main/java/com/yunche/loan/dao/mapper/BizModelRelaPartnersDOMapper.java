package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelRelaPartnersDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizModelRelaPartnersDOMapper {
    int deleteByPrimaryKey(BizModelRelaPartnersDO key);

    int insert(BizModelRelaPartnersDO record);

    int insertSelective(BizModelRelaPartnersDO record);
}