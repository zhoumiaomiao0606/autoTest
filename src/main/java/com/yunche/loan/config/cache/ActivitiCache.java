package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.activiti.engine.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/4/9
 */
@Component
public class ActivitiCache {

    private static final Map<String, List<String>> canditate = Maps.newHashMap();

    /**
     * activiti节点-候选组-缓存KEY
     */
    private static final String CANDIDATE_GROUP_CACHE_KEY = "activiti:candidate:group";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;


    @Autowired
    private FormService formService;

    @Autowired
    private IdentityService identityService;


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


        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask("");


//        identityLinksForTask.parallelStream()
//                .forEach();



        List<Group> list = identityService.createGroupQuery()
                .list();


        System.out.println(JSON.toJSONString(list));
        System.out.println();


    }

}
