package com.yunche.loan.service.impl;

import com.yunche.loan.mapper.ActivitiDeploymentMapper;
import com.yunche.loan.service.ActivitiVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.yunche.loan.service.impl.LoanProcessServiceImpl.NEW_LINE;

/**
 * @author liuzhe
 * @date 2018/5/4
 */
@Service
public class ActivitiVersionServiceImpl implements ActivitiVersionService {

    private static final Logger logger = LoggerFactory.getLogger(ActivitiVersionServiceImpl.class);

    @Autowired
    private ActivitiDeploymentMapper activitiDeploymentMapper;


    /**
     * 流程替换
     */
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

            // 删除最新的            流程定义
            int deleteNewVersionProcessDefinitionCount = activitiDeploymentMapper.deleteNewVersionProcessDefinition(newVersionDeploymentId);

            // 部署ID替换
            int replaceDeploymentIdCount = activitiDeploymentMapper.replaceDeploymentId(lastVersionDeploymentId, newVersionDeploymentId);

            logger.info("替换旧流程成功        >>>>>"
                            + NEW_LINE
                            + "lastVersionDeploymentId : {}, newVersionDeploymentId : {}, deleteAllBpmnAndPngCount : {}, deleteNewVersionProcessDefinitionCount : {}, replaceDeploymentIdCount : {}.",
                    lastVersionDeploymentId, newVersionDeploymentId, deleteAllBpmnAndPngCount, deleteNewVersionProcessDefinitionCount, replaceDeploymentIdCount);
        } else {

            logger.info("无旧版本流程       >>>>>"
                    + NEW_LINE
                    + "lastVersionDeploymentId : {}", lastVersionDeploymentId);
        }
    }
}
