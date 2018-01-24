package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.PartnerDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PartnerDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PartnerDO record);

    int insertSelective(PartnerDO record);

    PartnerDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PartnerDO record);

    int updateByPrimaryKey(PartnerDO record);
}