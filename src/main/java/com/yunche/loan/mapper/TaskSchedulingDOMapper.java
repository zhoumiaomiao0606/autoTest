package com.yunche.loan.mapper;

import com.yunche.loan.domain.vo.ScheduleTaskVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskSchedulingDOMapper {
    List<ScheduleTaskVO> selectScheduleTaskList(Long employeeId);
}
