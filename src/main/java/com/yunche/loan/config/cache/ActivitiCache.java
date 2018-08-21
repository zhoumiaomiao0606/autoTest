package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yunche.loan.mapper.ActivitiDeploymentMapper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.LoanDataFlowConst.DATA_FLOW_TASK_KEY_PREFIX;

/**
 * @author liuzhe
 * @date 2018/4/9
 */
@Component
public class ActivitiCache {

    /**
     * activiti节点-用户组-缓存KEY
     */
    private static final String CANDIDATE_GROUP_ALL_CACHE_KEY = "activiti:candidate:group:all";

    /**
     * data_flow：role-nodes  映射cache-key
     */
    private static final String CANDIDATE_GROUP_DATA_FLOW_CACHE_KEY = "activiti:candidate:group:data-flow";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ActivitiDeploymentMapper activitiDeploymentMapper;


    /**
     * all：node-roles 映射关系
     *
     * @return
     */
    public Map<String, List<String>> getNodeRolesMap() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CANDIDATE_GROUP_ALL_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }

        // 刷新cache
        refreshNodeRolesCache();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }
        return null;
    }

    /**
     * data_flow：role-nodes  映射关系
     *
     * @return
     */
    public Map<String, List<String>> getDataFlowRoleNodesMap() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CANDIDATE_GROUP_DATA_FLOW_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }

        // 刷新cache
        refreshDataFlowRoleNodesCache();

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
//    @PostConstruct
    public void refresh() {
        // node-roles  映射cache
        refreshNodeRolesCache();
        // data_flow：role-nodes  映射cache
        refreshDataFlowRoleNodesCache();
    }

    /**
     * node - roles 映射cache
     */
    private void refreshNodeRolesCache() {
        Map<String, List<String>> taskDefinitionKeyCandidateGroupsMap = getTaskDefinitionKeyCandidateGroupsMap();

        if (!CollectionUtils.isEmpty(taskDefinitionKeyCandidateGroupsMap)) {
            BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CANDIDATE_GROUP_ALL_CACHE_KEY);
            boundValueOps.set(JSON.toJSONString(taskDefinitionKeyCandidateGroupsMap));
        }
    }

    /**
     * data_flow：role-nodes 映射cache
     */
    private void refreshDataFlowRoleNodesCache() {
        // all：node-roles 映射关系
        Map<String, List<String>> nodeRolesMap = getNodeRolesMap();

        Map<String, Set<String>> roleNodesMap = Maps.newHashMap();

        if (!CollectionUtils.isEmpty(nodeRolesMap)) {

            nodeRolesMap.forEach((node, roles) -> {

                // 过滤data_flow节点key
                if (node.startsWith(DATA_FLOW_TASK_KEY_PREFIX)) {

                    if (!CollectionUtils.isEmpty(roles)) {

                        roles.stream()
                                .filter(Objects::nonNull)
                                .forEach(role -> {

                                    if (!roleNodesMap.containsKey(role)) {
                                        roleNodesMap.put(role, Sets.newHashSet(node));
                                    } else {
                                        Set<String> nodes = roleNodesMap.get(role);
                                        nodes.add(node);
                                    }

                                });
                    }
                }

            });
        }

        // set cache
        if (!CollectionUtils.isEmpty(roleNodesMap)) {
            BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CANDIDATE_GROUP_DATA_FLOW_CACHE_KEY);
            boundValueOps.set(JSON.toJSONString(roleNodesMap));
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

        Map<String, List<String>> taskDefinitionKeyCandidateGroupsMap = Maps.newHashMap();

        // getAll - processDefinitionResourceName
        Set<String> processDefinitionResourceNameSet = getAllResourceName();

        if (!CollectionUtils.isEmpty(processDefinitionResourceNameSet)) {

            processDefinitionResourceNameSet.stream()
                    .filter(StringUtils::isNotBlank)
                    .forEach(processDefinitionResourceName -> {

                        getAndSetTaskDefinitionKeyCandidateGroupsMap(taskDefinitionKeyCandidateGroupsMap, processDefinitionResourceName);
                    });
        }

        return taskDefinitionKeyCandidateGroupsMap;
    }

    /**
     * @param taskDefinitionKeyCandidateGroupsMap
     * @param processDefinitionResourceName
     * @return
     */
    private Map<String, List<String>> getAndSetTaskDefinitionKeyCandidateGroupsMap(Map<String, List<String>> taskDefinitionKeyCandidateGroupsMap,
                                                                                   String processDefinitionResourceName) {

        // 查询最新版本的流程定义
        ProcessDefinition lastVersionProcessDefinition = findLastVersionProcessDefinition(processDefinitionResourceName);

        if (null == lastVersionProcessDefinition) {
            return null;
        }

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
     * @param processDefinitionResourceName
     * @return
     */
    private ProcessDefinition findLastVersionProcessDefinition(String processDefinitionResourceName) {

        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionResourceName(processDefinitionResourceName)
                .orderByProcessDefinitionVersion()
                //使用流程定义的版本升序排列
                .desc()
                .listPage(0, 1);

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * Map<String, JSONArray>   ->   Map<String, Set<String>>
     *
     * @param map
     * @return
     */
    private Map<String, Set<String>> convertValJSONArray2Set(Map<String, JSONArray> map) {

        Map<String, Set<String>> dataFlowRoleNodesMap = Maps.newHashMap();

        map.forEach((k, v) -> {

            // JsonArray -> Set
            Set<String> vSet = v.stream().map(e -> {
                return (String) e;
            }).collect(Collectors.toSet());

            dataFlowRoleNodesMap.put(k, vSet);
        });

        return dataFlowRoleNodesMap;
    }

    public Set<String> getAllResourceName() {

        Set<String> resourceNameSet = activitiDeploymentMapper.getAllResourceName();

        return resourceNameSet;
    }
}
