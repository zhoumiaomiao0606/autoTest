package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.RspLawsuitDO;

public interface RspLawsuitDOMapper {
    int insert(RspLawsuitDO record);

    int insertSelective(RspLawsuitDO record);
}