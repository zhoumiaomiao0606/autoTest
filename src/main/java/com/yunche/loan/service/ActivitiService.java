package com.yunche.loan.service;

import org.activiti.engine.runtime.ProcessInstance;

import java.util.Set;

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

    /**
     * 更新Activiti指定流程的版本
     *
     * @param resourceName
     */
    void replaceActivitiVersion(String resourceName);

    /**
     * 获取登录用户->角色->有权操作的：资料流转节点
     *
     * @return
     */
    Set<String> getLoginUserOwnDataFlowNodes();
}
