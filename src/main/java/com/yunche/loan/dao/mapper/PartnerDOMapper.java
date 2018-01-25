package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.PartnerDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PartnerDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PartnerDO record);

    int insertSelective(PartnerDO record);

    PartnerDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(PartnerDO record);

    int updateByPrimaryKey(PartnerDO record);

    List<String> getAllName(@Param("status") Byte status);
}