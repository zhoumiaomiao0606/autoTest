package com.yunche.loan.service;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.AccommodationApplyParam;
import com.yunche.loan.domain.query.TaskListQuery;

public interface JinTouXingAccommodationApplyService {

    ResultBean batchLoan(AccommodationApplyParam param);

    ResultBean export(TaskListQuery taskListQuery);

    ResultBean detail(Long orderId);
}
