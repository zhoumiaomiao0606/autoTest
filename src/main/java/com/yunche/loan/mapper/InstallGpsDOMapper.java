package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.InstallGpsDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InstallGpsDOMapper {
    int deleteByPrimaryKey(Long id);

    int deleteByOrderId(Long orderId);

    int insertSelective(InstallGpsDO record);

    InstallGpsDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InstallGpsDO record);
}