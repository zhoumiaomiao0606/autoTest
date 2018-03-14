package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ApplyLicensePlateRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplyLicensePlateRecordDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(ApplyLicensePlateRecordDO record);

    ApplyLicensePlateRecordDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ApplyLicensePlateRecordDO record);
}