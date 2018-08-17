package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LitigationStateDO;

public interface LitigationStateDOMapper {
    int insert(LitigationStateDO record);

    int insertSelective(LitigationStateDO record);
}