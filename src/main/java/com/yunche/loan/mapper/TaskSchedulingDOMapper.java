package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.TaskListQuery;

import com.yunche.loan.domain.vo.ScheduleTaskVO;
import com.yunche.loan.domain.vo.TaskListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface TaskSchedulingDOMapper {

    List<ScheduleTaskVO> selectScheduleTaskList(@Param("key") String key,@Param("employeeId") Long employeeId);

    List<TaskListVO> selectAppTaskList(@Param("multipartType") Integer multipartType, @Param("customer") String customer,@Param("loginUserId") Long loginUserId);

    List<TaskListVO> selectTaskList(TaskListQuery taskListQuery);

}
