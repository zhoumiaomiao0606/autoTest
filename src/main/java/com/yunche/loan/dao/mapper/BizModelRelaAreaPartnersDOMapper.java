package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelRelaAreaPartnersDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizModelRelaAreaPartnersDOMapper {
    int delete(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);

    int insert(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);

    BizModelRelaAreaPartnersDO query(Long bizId, Long areaId, Long groupId);

    int update(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);
}