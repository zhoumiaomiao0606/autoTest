package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.MaterialAuditDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MaterialAuditDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(MaterialAuditDO record);

    MaterialAuditDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MaterialAuditDO record);
}