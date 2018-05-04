package com.yunche.loan.service.impl;

import com.yunche.loan.mapper.ActivitiDeploymentMapper;
import com.yunche.loan.service.ActivitiVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liuzhe
 * @date 2018/5/4
 */
@Service
public class ActivitiVersionServiceImpl implements ActivitiVersionService {

    @Autowired
    private ActivitiDeploymentMapper activitiDeploymentMapper;


    @Override
    @Transactional
    public void replaceActivitiVersion() {

        // 获取上个版本的部署ID
        Long lastVersionDeploymentId = activitiDeploymentMapper.getLastVersionDeploymentId();

        if (null != lastVersionDeploymentId) {

            // 获取(当前部署的)最新版本的部署ID
            Long newVersionDeploymentId = activitiDeploymentMapper.getNewVersionDeploymentId();

            // 仅保留新版本           资源文件(bpmn/png)
            int deleteAllBpmnAndPngCount = activitiDeploymentMapper.deleteAllBpmnAndPngExcludeNewVersion(newVersionDeploymentId);

            // 仅保留上个版本         流程定义
//            int deleteAllProcessDefinitionCount = activitiDeploymentMapper.deleteAllProcessDefinitionExcludeLastVersion(lastVersionDeploymentId);

            // 删除最新的            流程定义
            int deleteNewVersionProcessDefinitionCount = activitiDeploymentMapper.deleteNewVersionProcessDefinition(newVersionDeploymentId);

            // 部署ID替换   -将新流程图的ACT_GE_BYTEARRAY表数据里的DEPLOYMENT_ID_修改成旧流程图ACT_RE_PROCDEF表数据的DEPLOYMENT_ID_
            int replaceDeploymentIdCount = activitiDeploymentMapper.replaceDeploymentId(lastVersionDeploymentId, newVersionDeploymentId);
        }
    }
}
