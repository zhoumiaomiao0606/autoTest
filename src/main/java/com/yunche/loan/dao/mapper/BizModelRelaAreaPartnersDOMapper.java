package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelRelaAreaPartnersDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizModelRelaAreaPartnersDOMapper {
    int delete(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);

    int deleteByPrimaryKey(Long bizId, Long areaId, Long groupId);

    int insert(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);

    BizModelRelaAreaPartnersDO query(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);

    List<BizModelRelaAreaPartnersDO> queryById(Long bizId);

    int update(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);

    List<BizModelRelaAreaPartnersDO> listQuery(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);
}