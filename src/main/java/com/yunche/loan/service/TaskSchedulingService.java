package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.TaskListQuery;

public interface TaskSchedulingService {
    ResultBean scheduleTaskList(Integer pageIndex, Integer pageSize);

    ResultBean queryTaskList(TaskListQuery taskListQuery);

    ResultBean queryAppTaskList(AppTaskListQuery appTaskListQuery);

    ResultBean queryLoginUserLevel();

}
