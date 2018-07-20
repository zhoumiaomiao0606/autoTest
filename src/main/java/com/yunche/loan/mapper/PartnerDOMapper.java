package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.PartnerQuery;
import com.yunche.loan.domain.entity.PartnerDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PartnerDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(PartnerDO record);

    int insertSelective(PartnerDO record);

    PartnerDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    List<PartnerDO> batchSelectByPrimaryKey(@Param("idList") List<Long> idList, @Param("status") Byte status);

    int updateByPrimaryKeySelective(PartnerDO record);

    int updateByPrimaryKey(PartnerDO record);

    List<String> getAllName(@Param("status") Byte status);

    int count(PartnerQuery query);

    List<PartnerDO> query(PartnerQuery query);

    List<PartnerDO> getAll(@Param("status") Byte status);
}