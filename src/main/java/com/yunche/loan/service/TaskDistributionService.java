package com.yunche.loan.service;

import com.yunche.loan.domain.vo.TaskDisVO;

import java.util.List;

public interface TaskDistributionService {
    void get(Long taskId,String taskkey);

    void release(Long taskId,String taskKey);

    void finish(Long taskId,String taskKey);

    TaskDisVO query(Long taskId, String taskKey);

    public void rejectFinish(Long taskId,Long orderId,List<String> taskKeys);
}
