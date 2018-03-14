package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.RemitDetailsDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RemitDetailsDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(RemitDetailsDO record);

    RemitDetailsDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RemitDetailsDO record);
}