package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.AppTaskVO;
import com.yunche.loan.domain.vo.ScheduleTaskVO;
import com.yunche.loan.domain.vo.TaskListVO;

import java.util.List;

public interface TaskSchedulingService {

    ResultBean<List<ScheduleTaskVO>> scheduleTaskList(Integer pageIndex, Integer pageSize);

    ResultBean<List<ScheduleTaskVO>> scheduleTaskListBykey(String key, Integer pageIndex, Integer pageSize);

    ResultBean<List<TaskListVO>> queryTaskList(TaskListQuery taskListQuery);

    ResultBean<Long> countQueryTaskList(TaskListQuery taskListQuery);

    ResultBean<List<AppTaskVO>> queryAppTaskList(AppTaskListQuery appTaskListQuery);

    ResultBean<List<TaskListVO>> queryDataFlowTaskList(TaskListQuery taskListQuery);
}
