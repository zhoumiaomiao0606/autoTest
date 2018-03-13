package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.MaterialAuditDO;

public interface MaterialAuditDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(MaterialAuditDO record);

    MaterialAuditDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MaterialAuditDO record);
}