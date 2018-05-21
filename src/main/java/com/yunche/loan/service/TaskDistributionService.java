package com.yunche.loan.service;

import com.yunche.loan.domain.vo.TaskDisVO;

public interface TaskDistributionService {
    void get(Long taskId,String taskkey);

    void release(Long taskId,String taskKey);

    void finish(Long taskId,String taskKey);

    TaskDisVO query(Long taskId, String taskKey);
}
