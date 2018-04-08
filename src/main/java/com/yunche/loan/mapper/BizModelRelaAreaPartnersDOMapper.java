package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BizModelRelaAreaPartnersDO;
import com.yunche.loan.domain.query.BizModelQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BizModelRelaAreaPartnersDOMapper {

    int deleteByPrimaryKey(BizModelRelaAreaPartnersDO key);

    int insert(BizModelRelaAreaPartnersDO record);

    int insertSelective(BizModelRelaAreaPartnersDO record);

    BizModelRelaAreaPartnersDO selectByPrimaryKey(BizModelRelaAreaPartnersDO key);

    int updateByPrimaryKeySelective(BizModelRelaAreaPartnersDO record);

    int updateByPrimaryKey(BizModelRelaAreaPartnersDO record);

    int delete(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);

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

    /**
     * 根据bizId删除
     *
     * @param bizId
     * @return
     */
    int deleteByBizId(Long bizId);
}