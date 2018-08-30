package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.FlowOperationMsgParam;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.*;

import java.util.List;
import java.util.Map;

public interface TaskSchedulingService {

    ResultBean<Map> appCount();

    ResultBean<Long> countFlowOperationMsgList(Long multipartType);

    ResultBean<List<FlowOperationMsgListVO>> selectFlowOperationMsgList(FlowOperationMsgParam param);

    boolean selectRejectTask(Long orderId);

    ResultBean<List<ScheduleTaskVO>> scheduleTaskList(String key,Integer pageIndex, Integer pageSize);

    ResultBean<List<TaskListVO>> queryTaskList(TaskListQuery taskListQuery);

    ResultBean<Long> countQueryTaskList(TaskListQuery taskListQuery);

    ResultBean<List<AppTaskVO>> queryAppTaskList(AppTaskListQuery appTaskListQuery);

    ResultBean<Long> countScheduletasklist();
}
