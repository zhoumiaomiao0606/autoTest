package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ConfThirdPartyMoneyDO;
import com.yunche.loan.domain.query.ConfThirdPartyMoneyQuery;

import java.util.List;

public interface ConfThirdPartyMoneyDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ConfThirdPartyMoneyDO record);

    int insertSelective(ConfThirdPartyMoneyDO record);

    ConfThirdPartyMoneyDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ConfThirdPartyMoneyDO record);

    int updateByPrimaryKey(ConfThirdPartyMoneyDO record);

    List<ConfThirdPartyMoneyDO> query(ConfThirdPartyMoneyQuery query);
}