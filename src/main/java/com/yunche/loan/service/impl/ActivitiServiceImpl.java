package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.service.ActivitiService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuzhe
 * @date 2018/8/21
 */
@Service
public class ActivitiServiceImpl implements ActivitiService {

    @Autowired
    private RuntimeService runtimeService;


    @Override
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey) {

        // 开启activiti流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);

        Preconditions.checkNotNull(processInstance, "开启流程实例异常");
        Preconditions.checkNotNull(processInstance.getProcessInstanceId(), "开启流程实例异常");

        return processInstance;
    }
}
