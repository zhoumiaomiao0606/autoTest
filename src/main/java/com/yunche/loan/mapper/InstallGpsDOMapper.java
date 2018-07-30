package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.InstallGpsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InstallGpsDOMapper {
    int deleteByPrimaryKey(Long id);

    int deleteByOrderId(Long orderId);

    int insertSelective(InstallGpsDO record);

    InstallGpsDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InstallGpsDO record);

    int selectBygpsNumber(@Param("gps_number") String gps_number);
}