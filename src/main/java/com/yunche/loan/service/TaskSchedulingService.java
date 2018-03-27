package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.TaskListQuery;

public interface TaskSchedulingService {
    public ResultBean scheduleTaskList(Integer pageIndex, Integer pageSize);

    public ResultBean queryTaskList(TaskListQuery taskListQuery);
}
