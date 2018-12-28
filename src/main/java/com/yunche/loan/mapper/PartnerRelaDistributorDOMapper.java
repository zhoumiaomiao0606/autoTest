package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.PartnerRelaDistributorDO;
import com.yunche.loan.domain.entity.PartnerRelaDistributorDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartnerRelaDistributorDOMapper {
    int deleteByPrimaryKey(PartnerRelaDistributorDOKey key);

    int insert(PartnerRelaDistributorDO record);

    int insertSelective(PartnerRelaDistributorDO record);

    PartnerRelaDistributorDO selectByPrimaryKey(PartnerRelaDistributorDOKey key);

    int updateByPrimaryKeySelective(PartnerRelaDistributorDO record);

    int updateByPrimaryKey(PartnerRelaDistributorDO record);


    List<PartnerRelaDistributorDO> listDistributorByPartner(Long partnerId);
}