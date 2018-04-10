package com.yunche.loan.service;

/**
 * @author liuzhe
 * @date 2018/4/10
 */
public interface PermissionService {

    /**
     * 校验任务节点的权限
     *
     * @param taskDefinitionKey
     */
    void checkTaskPermission(String taskDefinitionKey);
}
