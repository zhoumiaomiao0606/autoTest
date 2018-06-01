package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.TaskDistributionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskDistributionDOMapper {
    int deleteByPrimaryKey(@Param("taskId") Long taskId,@Param("taskKey") String taskKey);

    int insertSelective(TaskDistributionDO record);

    TaskDistributionDO selectByPrimaryKey(@Param("taskId") Long taskId,@Param("taskKey") String taskKey);

    TaskDistributionDO selectByPrimaryKeyAndStatus(@Param("taskId") Long taskId,@Param("taskKey") String taskKey);

    int updateByPrimaryKeySelective(TaskDistributionDO record);
}