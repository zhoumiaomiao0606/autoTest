package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ReportPowerDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ReportPowerDOMapper {
    int insert(ReportPowerDO record);

    int insertSelective(ReportPowerDO record);

    List<String> selectPointByGroupName(@Param("groupNames")Set<String> groupNames);
}