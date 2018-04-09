package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liuzhe
 * @date 2018/4/9
 */
@Component
public class ActivitiCache {

    /**
     * activiti节点-候选组-缓存KEY
     */
    private static final String CANDIDATE_GROUP_CACHE_KEY = "activiti:candidate:group";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RepositoryService repositoryService;


    public Map<String, List<String>> get() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CANDIDATE_GROUP_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }

        // 刷新缓存
        refresh();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }
        return null;
    }

    /**
     * 刷新缓存
     */
    public void refresh() {

        Map<String, List<String>> taskDefinitionKeyCandidateGroupsMap = getTaskDefinitionKeyCandidateGroupsMap();

        if (!CollectionUtils.isEmpty(taskDefinitionKeyCandidateGroupsMap)) {
            BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CANDIDATE_GROUP_CACHE_KEY);
            boundValueOps.set(JSON.toJSONString(taskDefinitionKeyCandidateGroupsMap));
        }
    }

    /**
     * 解析xx.bpmn文件，遍历出UserTask节点, 并按照id（taskDefinitionKey）分类存储流程列表。
     * <p>
     * KEY   :    taskDefinitionKey
     * VALUE :    candidateGroups
     *
     * @return
     */
    public Map<String, List<String>> getTaskDefinitionKeyCandidateGroupsMap() {

        // 查询最新版本的流程定义
        ProcessDefinition lastVersionProcessDefinition = findLastVersionProcessDefinition();

        if (null == lastVersionProcessDefinition) {
            return null;
        }
        Map<String, List<String>> taskDefinitionKeyCandidateGroupsMap = Maps.newHashMap();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(lastVersionProcessDefinition.getId());

        List<Process> processList = bpmnModel.getProcesses();

        if (!CollectionUtils.isEmpty(processList)) {

            for (Process process : processList) {

                if (process != null) {

                    Collection<FlowElement> flowElementCollection = process.getFlowElements();

                    if (!CollectionUtils.isEmpty(flowElementCollection)) {

                        for (FlowElement flowElement : flowElementCollection) {

                            if (flowElement instanceof UserTask) {

                                UserTask userTask = (UserTask) flowElement;

                                String taskDefinitionKey = userTask.getId();
                                List<String> candidateGroups = userTask.getCandidateGroups();

                                taskDefinitionKeyCandidateGroupsMap.put(taskDefinitionKey, candidateGroups);
                            }
                        }
                    }
                }
            }
        }

        return taskDefinitionKeyCandidateGroupsMap;
    }

    /**
     * 查询最新版本的流程定义
     *
     * @return
     */
    private ProcessDefinition findLastVersionProcessDefinition() {

        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion()
                //使用流程定义的版本升序排列
                .desc()
                .listPage(0, 1);

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }
}
