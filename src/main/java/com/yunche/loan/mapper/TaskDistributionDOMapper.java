package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.TaskDistributionDO;
import org.apache.ibatis.annotations.Param;

public interface TaskDistributionDOMapper {

    TaskDistributionDO selectLastTaskDistributionGroupByTaskKey(@Param("taskId") Long taskId, @Param("taskKey") String taskKey);

    int deleteByPrimaryKey(Long id);

    int insertSelective(TaskDistributionDO record);

    TaskDistributionDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TaskDistributionDO record);
}