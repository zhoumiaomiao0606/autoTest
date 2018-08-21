package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.yunche.loan.config.cache.ActivitiCache;
import com.yunche.loan.mapper.ActivitiDeploymentMapper;
import com.yunche.loan.service.ActivitiService;
import com.yunche.loan.service.PermissionService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.yunche.loan.service.impl.LoanProcessServiceImpl.NEW_LINE;

/**
 * @author liuzhe
 * @date 2018/8/21
 */
@Service
public class ActivitiServiceImpl implements ActivitiService {

    private static final Logger logger = LoggerFactory.getLogger(ActivitiServiceImpl.class);

    @Autowired
    private ActivitiDeploymentMapper activitiDeploymentMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ActivitiCache activitiCache;


    @Override
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey) {

        // 开启activiti流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);

        Preconditions.checkNotNull(processInstance, "开启流程实例异常");
        Preconditions.checkNotNull(processInstance.getProcessInstanceId(), "开启流程实例异常");

        return processInstance;
    }

    /**
     * 流程替换
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceActivitiVersion(@NotEmpty(message = "resourceName不能为空") String resourceName) {

        // 获取上个版本的部署ID
        Long lastVersionDeploymentId = activitiDeploymentMapper.getLastVersionDeploymentId(resourceName);

        if (null != lastVersionDeploymentId) {

            // 获取(当前部署的)最新版本的部署ID
            Long newVersionDeploymentId = activitiDeploymentMapper.getNewVersionDeploymentId(resourceName);

            // 仅保留新版本           资源文件(bpmn/png)
            int deleteAllBpmnAndPngCount = activitiDeploymentMapper.deleteAllBpmnAndPngExcludeNewVersion(newVersionDeploymentId, resourceName);

            // 删除最新的            流程定义
            int deleteNewVersionProcessDefinitionCount = activitiDeploymentMapper.deleteNewVersionProcessDefinition(newVersionDeploymentId);

            // 部署ID替换
            int replaceDeploymentIdCount = activitiDeploymentMapper.replaceDeploymentId(lastVersionDeploymentId, newVersionDeploymentId);

            logger.info("替换旧流程成功        >>>>>       resourceName : {}"
                            + NEW_LINE
                            + "lastVersionDeploymentId : {}, newVersionDeploymentId : {}, deleteAllBpmnAndPngCount : {}, deleteNewVersionProcessDefinitionCount : {}, replaceDeploymentIdCount : {}.",
                    resourceName,
                    lastVersionDeploymentId, newVersionDeploymentId, deleteAllBpmnAndPngCount, deleteNewVersionProcessDefinitionCount, replaceDeploymentIdCount);
        } else {

            logger.info("无旧版本流程       >>>>>       resourceName : {}"
                    + NEW_LINE
                    + "lastVersionDeploymentId : {}", resourceName, lastVersionDeploymentId);
        }
    }

    @Override
    public Set<String> getLoginUserOwnDataFlowNodes() {

        Set<String> loginUserHasUserGroups = permissionService.getLoginUserHasUserGroups();

        Set<String> userOwnDataFlowNodes = Sets.newHashSet();

        Map<String, List<String>> dataFlowRoleNodesMap = activitiCache.getDataFlowRoleNodesMap();

        if (!CollectionUtils.isEmpty(dataFlowRoleNodesMap)) {

            dataFlowRoleNodesMap.forEach((role, nodes) -> {

                if (loginUserHasUserGroups.contains(role)) {
                    userOwnDataFlowNodes.addAll(nodes);
                }
            });
        }

        return userOwnDataFlowNodes;
    }
}
