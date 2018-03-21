package com.yunche.loan.service;

import com.yunche.loan.domain.vo.ScheduleTaskVO;

import java.util.List;

public interface TaskSchedulingService {
    public List<ScheduleTaskVO> scheduleTaskList();
}
