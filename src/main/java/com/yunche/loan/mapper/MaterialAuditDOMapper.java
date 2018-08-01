package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.MaterialAuditDO;
import com.yunche.loan.domain.param.MaterialDownloadParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialAuditDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MaterialAuditDO record);

    int insertSelective(MaterialAuditDO record);

    MaterialAuditDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MaterialAuditDO record);

    int updateByPrimaryKey(MaterialAuditDO record);

    List<MaterialDownloadParam> selectDownloadMaterial(@Param(value = "orderId") Long orderId,
                                                       @Param(value = "customerId") Long customerId);

}