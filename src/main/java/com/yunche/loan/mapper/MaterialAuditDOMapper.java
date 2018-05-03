package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.MaterialAuditDO;
import com.yunche.loan.domain.param.MaterialDownloadParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MaterialAuditDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(MaterialAuditDO record);

    MaterialAuditDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MaterialAuditDO record);

    List<MaterialDownloadParam> selectDownloadMaterial(@Param(value = "orderId")Long orderId,@Param(value = "customerId") String customerId);
}