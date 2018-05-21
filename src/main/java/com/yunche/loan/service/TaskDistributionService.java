package com.yunche.loan.service;

public interface TaskDistributionService {
    void get(Long taskId,String taskkey);

    void release(Long taskId,String taskkey);

    void finish(Long taskId,String taskkey);
}
