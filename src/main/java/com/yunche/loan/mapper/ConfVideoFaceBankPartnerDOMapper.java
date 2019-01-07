package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ConfVideoFaceBankPartnerDO;
import com.yunche.loan.domain.query.ConfVideoFaceBankPartnerQuery;

import java.util.List;

public interface ConfVideoFaceBankPartnerDOMapper {

    int deleteByPrimaryKey(ConfVideoFaceBankPartnerDO key);

    int insert(ConfVideoFaceBankPartnerDO record);

    int insertSelective(ConfVideoFaceBankPartnerDO record);

    ConfVideoFaceBankPartnerDO selectByPrimaryKey(ConfVideoFaceBankPartnerDO key);

    int updateByPrimaryKeySelective(ConfVideoFaceBankPartnerDO record);

    int updateByPrimaryKey(ConfVideoFaceBankPartnerDO record);

    List<ConfVideoFaceBankPartnerDO> query(ConfVideoFaceBankPartnerQuery query);
}