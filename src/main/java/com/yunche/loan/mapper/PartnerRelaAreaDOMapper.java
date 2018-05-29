package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.PartnerRelaAreaDO;
import com.yunche.loan.domain.entity.PartnerRelaAreaDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartnerRelaAreaDOMapper {
    int deleteByPrimaryKey(PartnerRelaAreaDOKey key);

    int insert(PartnerRelaAreaDO record);

    int insertSelective(PartnerRelaAreaDO record);

    PartnerRelaAreaDO selectByPrimaryKey(PartnerRelaAreaDOKey key);

    int updateByPrimaryKeySelective(PartnerRelaAreaDO record);

    int updateByPrimaryKey(PartnerRelaAreaDO record);

    int deleteAllByPartnerId(Long partnerId);

    List<Long> getAreaIdListByPartnerId(Long partnerId);
}