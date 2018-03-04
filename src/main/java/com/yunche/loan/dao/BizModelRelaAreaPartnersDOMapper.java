package com.yunche.loan.dao;

import com.yunche.loan.domain.entity.BizModelRelaAreaPartnersDO;
import com.yunche.loan.domain.query.BizModelQuery;
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

    /**
     * 获取所有AreaId
     *
     * @return
     */
    List<Long> getAllAreaId();

    /**
     * 根据条件 获取所有的bizModelId
     *
     * @param query
     * @return
     */
    List<Long> getBizModelIdListByCondition(BizModelQuery query);
}