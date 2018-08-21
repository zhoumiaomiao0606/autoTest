package com.yunche.loan.service;

import org.activiti.engine.runtime.ProcessInstance;

/**
 * @author liuzhe
 * @date 2018/8/21
 */
public interface ActivitiService {

    /**
     * 开启activiti流程
     *
     * @param processDefinitionKey
     * @return
     */
    ProcessInstance startProcessInstanceByKey(String processDefinitionKey);
}
