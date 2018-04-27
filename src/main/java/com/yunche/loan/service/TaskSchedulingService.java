package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.AppTaskVO;

import java.util.List;

public interface TaskSchedulingService {
    ResultBean scheduleTaskList(String key,Integer pageIndex, Integer pageSize);

    ResultBean queryTaskList(TaskListQuery taskListQuery);

    ResultBean<List<AppTaskVO>> queryAppTaskList(AppTaskListQuery appTaskListQuery);

    ResultBean queryLoginUserLevel();

}
