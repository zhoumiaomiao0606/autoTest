package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.PartnerWhiteListDO;
import com.yunche.loan.domain.entity.PartnerWhiteListDOKey;

import java.util.List;

public interface PartnerWhiteListDOMapper {
    int deleteByPrimaryKey(PartnerWhiteListDOKey key);

    int insert(PartnerWhiteListDO record);

    int insertSelective(PartnerWhiteListDO record);

    PartnerWhiteListDO selectByPrimaryKey(PartnerWhiteListDOKey key);

    List<PartnerWhiteListDO> selectByOperationType(String operationType);

    int updateByPrimaryKeySelective(PartnerWhiteListDO record);

    int updateByPrimaryKey(PartnerWhiteListDO record);
}