package com.yunche.loan.service;

import java.util.Set;

/**
 * @author liuzhe
 * @date 2018/5/4
 */
public interface ActivitiVersionService {

    /**
     * 更新Activiti版本
     */
    void replaceActivitiVersion();

    /**
     * 获取登录用户->角色->有权操作的：资料流转节点
     *
     * @return
     */
    Set<String> getLoginUserOwnDataFlowNodes();
}
