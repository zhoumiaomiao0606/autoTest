package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;

public interface TaskSchedulingService {
    public ResultBean scheduleTaskList(Integer startRow, Integer pageSize);
}
