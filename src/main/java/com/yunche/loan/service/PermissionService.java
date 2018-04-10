package com.yunche.loan.service;

import java.util.Set;

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

    /**
     * 获取当前登录用户的用户组名称列表
     *
     * @return
     */
    Set<String> getUserGroupNameSet();
}